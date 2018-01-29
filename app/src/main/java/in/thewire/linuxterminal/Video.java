package in.thewire.linuxterminal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class Video extends Fragment {
    private static final String WIFI = "Wi-Fi";
    private static final String ANY = "Any";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String URLhome =
            "https://thewire.in/feed/";
    private static final String URLvideo =
            "https://thewire.in/category/video/feed/";
    private static final String URLvideoHindi =
            "http://thewirehindi.com/category/वीडियो/feed";
    private static final String URLvideoHindioff =
            "http://thewirehindi.com/category/वीडियो/feed/?paged=";
    private static final String offsetURL =
            "https://thewire.in/category/video/feed/?paged=";
    private static String sPref = null;
    private RecyclerView slider;
    private LinearLayoutManager linearLayoutManager;
    private VideoAdapter videoAdapter;
    private List<WireParser.Entry> videoentries;
    private boolean pref;
    private ProgressBar progressBar;
    private String mParam2;
    private NetworkReceiver receiver = new NetworkReceiver();
    private Parcelable recyclerViewState;


    public Video() {
    }

    public static Video newInstance() {
        Video fragment = new Video();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int mParam1 = getArguments().getInt(ARG_PARAM1);
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        slider = (RecyclerView) view.findViewById(R.id.recyclerview);
        slider.setNestedScrollingEnabled(true);
        slider.setHasFixedSize(true);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        pref = sharedPrefs.getBoolean("summaryPref", false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        EndlessScrollListener scrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        slider.addOnScrollListener(scrollListener);
        //loadPage(URLhome);
        if (pref) {
            loadPage(URLvideoHindi);
        } else {
            loadPage(URLvideo);
        }
        return view;
    }


    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        boolean mobileConnected = false;
        boolean wifiConnected = false;
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
            //Log.d("response",conn.getResponseMessage());
            //Log.d("code",conn.getResponseCode()+"");
        }
        return conn.getInputStream();
    }

    private void loadNextDataFromApi(int offset) {
        offset++;
        String url;
        if (pref) {
            url = URLvideoHindioff + offset;
        } else {
            url = offsetURL + offset;
        }
        //Log.d("URL",url);
        new DownloadNextPage().execute(url);
    }

    private String loadXmlForOffset(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        WireParser stackOverflowXmlParser = new WireParser();

        int count = 0;

        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.page_title)).append("</h3>");
        htmlString.append("<em>").append(getResources().getString(R.string.updated)).append(" ").append(formatter.format(rightNow.getTime())).append("</em>");

        List<WireParser.Entry> entries;
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
            videoentries.add(videoentries.size(), entry);

            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        stream.close();
        return htmlString.toString();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Exception";
            } catch (XmlPullParserException e) {
                Log.d("Error", e + "");
                return "Exception XML";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            videoAdapter = new VideoAdapter(getContext(), videoentries);
            slider.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);


            slider.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerViewState = slider.getLayoutManager().onSaveInstanceState();
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


            videoAdapter.notifyItemInserted(videoentries.size());
            videoAdapter.notifyDataSetChanged();
            //slider.scrollToPosition(belowEntries.size()-1);
            slider.getLayoutManager().onRestoreInstanceState(recyclerViewState);


        }
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


            boolean refreshDisplay = true;
            refreshDisplay = WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI || ANY.equals(sPref) && networkInfo != null;
//Toast.makeText(context, "Connection Lost", Toast.LENGTH_SHORT).show();
        }
    }

}
