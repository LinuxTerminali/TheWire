package in.thewire.linuxterminal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IndividualActivity extends AppCompatActivity {
    private static final String URLCAT =
            "https://thewire.in/category/";
    private static String URLOffset =
            "https://thewire.in/category/";
    private RecyclerView slider;
    private LinearLayoutManager linearLayoutManager;
    private SearchAdapter searchAdapter;
    private List<WireParser.Entry> videoentries;
    private InputStream stream = null;
    private ProgressBar progressBar;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(IndividualActivity.this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);
        Intent intent = getIntent();
        String nam = intent.getStringExtra("category");
        Log.d("name", nam);
        String query;
        progressBar = (ProgressBar) findViewById(R.id.progress);

        if (pref) {
            String URLHINDICAT = "http://thewirehindi.com/category/";
            query = URLHINDICAT + nam + "/feed/";
            URLOffset = URLOffset + query + "?paged=";
            loadPage(URLHINDICAT + query);
        } else {
            query = setupurl(nam) + "/feed/";
            URLOffset = URLOffset + query + "?paged=";
            loadPage(URLCAT + query);
        }
        toolbar.setTitle(nam);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //loadPage(URLCAT+query);
        // Log.d("name", query);

        slider = (RecyclerView) findViewById(R.id.recyclerview);
        slider.setNestedScrollingEnabled(true);
        slider.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(IndividualActivity.this);
        EndlessScrollListener scrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        slider.addOnScrollListener(scrollListener);
        //loadPage(URLOffset);
    }

    private String setupurl(String text) {
        switch (text) {
            case "Environment":
                text = "environment-2";
                break;
            case "Indian Diplomacy":
                text = "external-affairs/diplomacy";
                break;
            case "South Asia":
                text = "external-affairs/south-asia";
                break;
            case "Cities & Architecture":
                text = "society/cities";
                break;
            case "External Affairs":
                text = "external-affairs";
                break;
        }
        return text;
    }


    private void loadPage(String URL) {
        /*if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass*/
        new DownloadXmlTask().execute(URL);
        /*} else {
            Toast.makeText(HomeActivity.this,"Check your network connection",Toast.LENGTH_SHORT).show();
        }*/
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {

        WireParser stackOverflowXmlParser = new WireParser();
        List<WireParser.Entry> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        videoentries = new ArrayList<>();
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        // Checks whether the user set the preference to include summary text
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(IndividualActivity.this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.page_title)).append("</h3>");
        htmlString.append("<em>").append(getResources().getString(R.string.updated)).append(" ").append(formatter.format(rightNow.getTime())).append("</em>");

        try {
            stream = downloadUrl(urlString);
            entries = stackOverflowXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IllegalStateException e) {
                    System.out.println("Error " + e.getMessage());
                    return null;
                }
            }
        }


        for (WireParser.Entry entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);
            htmlString.append("'>").append(entry.title).append("</a></p>");

            //Log.d("text",entry.link+" "+entry.title+" "+entry.summary);

            videoentries.add(entry);
            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        return htmlString.toString();
    }

    private InputStream downloadUrl(String urlString) throws IOException {

        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        //InputStream stream = ;
        /*if(conn.getInputStream()!=null){
            Log.d("response",conn.getResponseMessage());
            Log.d("code",conn.getResponseCode()+"");
        }*/


        return conn.getInputStream();
    }

    private void loadNextDataFromApi(int offset) {
        offset++;

        String url = URLOffset + offset;
        //Log.d("URLB",url);
        new DownloadNextPage().execute(url);
    }

    private String loadXmlForOffset(String urlString) throws XmlPullParserException, IOException {
        WireParser stackOverflowXmlParser = new WireParser();

        int count = 0;

        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(IndividualActivity.this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.page_title)).append("</h3>");
        htmlString.append("<em>").append(getResources().getString(R.string.updated)).append(" ").append(formatter.format(rightNow.getTime())).append("</em>");

        List<WireParser.Entry> entries;
        try {
            stream = downloadUrl(urlString);
            //Log.d("Stream",stream+"");
            entries = stackOverflowXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IllegalStateException e) {
                    System.out.println("Error " + e.getMessage());
                    return null;
                }
            }
        }


        for (WireParser.Entry entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);

            htmlString.append("'>").append(entry.title).append("</a></p>");
            videoentries.add(videoentries.size(), entry);

            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        //stream.close();
        return htmlString.toString();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Exception";
            } catch (XmlPullParserException e) {
                //Log.d("Error",e+"");
                return "Exception XML";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            searchAdapter = new SearchAdapter(IndividualActivity.this, videoentries);
            // Attach the adapter to the recyclerview to populate items
            slider.setAdapter(searchAdapter);
            progressBar.setVisibility(View.GONE);
            searchAdapter.notifyDataSetChanged();

            // Set layout manager to position the items
            //Log.d("modal",modalsList.toString());


            slider.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerViewState = slider.getLayoutManager().onSaveInstanceState();


            //  recyclerViewState = slider.getLayoutManager().onSaveInstanceState();
        }
    }

    private class DownloadNextPage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlForOffset(urls[0]);
            } catch (IOException e) {
                return "Exception";
            } catch (XmlPullParserException e) {
                //Log.d("Error",e+"");
                return "Exception XML";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            searchAdapter.notifyItemInserted(videoentries.size());
            searchAdapter.notifyDataSetChanged();
            //slider.scrollToPosition(belowEntries.size()-1);
            slider.getLayoutManager().onRestoreInstanceState(recyclerViewState);


        }
    }

}
