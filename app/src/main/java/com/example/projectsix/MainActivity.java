package com.example.projectsix;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>> {
    private NewsAdapter newsAdapter;
    private TextView emptyTextView;
    private ProgressBar progressBar;

    private static final String REQUEST_URL = "https://content.guardianapis.com/search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        emptyTextView = findViewById(R.id.text_view);

        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo NI = CM.getActiveNetworkInfo();
        if (NI != null && NI.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, this);

        } else {
            progressBar.setVisibility(View.GONE);
            emptyTextView.setText("No Internet Connection");
        }

        newsAdapter = new NewsAdapter(this, new ArrayList<NewsData>());
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(newsAdapter);
        listView.setEmptyView(emptyTextView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NewsData current = newsAdapter.getItem(position);

                Uri news_Uri = Uri.parse(current.getWebURL());

                Intent website = new Intent(Intent.ACTION_VIEW, news_Uri);
                startActivity(website);
            }
        });


    }

    //Loaders override methods below
    @Override
    public Loader<List<NewsData>> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("show-tags","contributor");
        uriBuilder.appendQueryParameter("api-key","94025501-2075-4d59-af41-ad8f82b2483f");
        return new NewsLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsData>> loader, List<NewsData> news) {

        newsAdapter.clear();
        progressBar.setVisibility(View.GONE);
        emptyTextView.setText("No News found");


        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsData>> loader) {

        newsAdapter.clear();
    }
}
