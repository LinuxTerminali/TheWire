package in.thewire.linuxterminal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

public class SearchResult extends AppCompatActivity {
    private static final String URLSEARCH =
            "https://thewire.in/feed/?s=";
    private static final String URLSEARCHHindi =
            "https://thewirehindi.com/feed/?s=";
    private RecyclerView slider;
    private LinearLayoutManager linearLayoutManager;
    private List<WireParser.Entry> videoentries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String nam = intent.getStringExtra("selected");
        Log.d("name", nam);
        String query = nam.replaceAll(" ", "+");

        loadPage(URLSEARCH + query);
        Log.d("name", URLSEARCH + query);

        slider = (RecyclerView) findViewById(R.id.recyclerview);
        slider.setNestedScrollingEnabled(true);
        slider.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(SearchResult.this);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(SearchResult.this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);
        if (pref) {
            loadPage(URLSEARCHHindi + query);
        } else {
            loadPage(URLSEARCH + query);
        }

    }

    private void loadPage(String URL) {

        new DownloadXmlTask().execute(URL);

    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        WireParser stackOverflowXmlParser = new WireParser();
        List<WireParser.Entry> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        videoentries = new ArrayList<>();
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(SearchResult.this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.page_title)).append("</h3>");
        htmlString.append("<em>").append(getResources().getString(R.string.updated)).append(" ").append(formatter.format(rightNow.getTime())).append("</em>");

        try {
            stream = downloadUrl(urlString);
            entries = stackOverflowXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        for (WireParser.Entry entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);
            htmlString.append("'>").append(entry.title).append("</a></p>");


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
        if (conn.getInputStream() != null) {
            // Log.d("response",conn.getResponseMessage());
            //Log.d("code",conn.getResponseCode()+"");
        }
        return conn.getInputStream();
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
            if (videoentries.size() == 0) {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(SearchResult.this);
                alertDialog.setMessage("No result found");
                alertDialog.setTitle("Sorry!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            } else {
                SearchAdapter searchAdapter = new SearchAdapter(SearchResult.this, videoentries);
                slider.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();


                slider.setLayoutManager(linearLayoutManager);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
            }
            //  recyclerViewState = slider.getLayoutManager().onSaveInstanceState();
        }
    }
}
