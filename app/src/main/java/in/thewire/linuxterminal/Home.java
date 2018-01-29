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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;
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


public class Home extends Fragment {

    private static final String WIFI = "Wi-Fi";
    private static final String ANY = "Any";
    private static final String ARG_PARAM1 = "param1";
    private static final String URLhome =
            "https://thewire.in/feed/";
    private static final String URLvideo =
            "https://thewire.in/category/video/feed/";
    private static final String offsetURL =
            "https://thewire.in/feed/?paged=";
    private static final String URLHOMEHINDI = "http://thewirehindi.com/feed/";
    private static final String URLHOMEHINDIoff = "http://thewirehindi.com/feed/?paged=";
    private static String sPref = null;
    private RecyclerView slider;
    ViewPager mViewPager;
    private LinearLayoutManager linearLayoutManager;
    private HomeAdapter adapter;
    private WireParser stackOverflowXmlParser;
    List<Modals> modalsList;
    TwoWayView lvTest;
    private List<WireParser.Entry> entries;
    private List<WireParser.Entry> sliderEntries;
    private List<WireParser.Entry> belowEntries;
    private boolean pref;
    private ProgressBar progressBar;
    private String mParam2;
    private NetworkReceiver receiver = new NetworkReceiver();
    private Parcelable recyclerViewState;

    public Home() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance() {
        Home fragment = new Home();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        slider = (RecyclerView) view.findViewById(R.id.recyclerview);
        slider.setNestedScrollingEnabled(true);
        slider.setHasFixedSize(true);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref = sharedPrefs.getBoolean("summaryPref", false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        EndlessScrollListener scrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        slider.addOnScrollListener(scrollListener);

        if (pref) {
            loadPage(URLHOMEHINDI);
        } else {
            loadPage(URLhome);

        }
        return view;

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

    private void loadNextDataFromApi(int offset) {
        offset++;
        String url;
        if (pref) {
            url = URLHOMEHINDIoff + offset;
        } else {
            url = offsetURL + offset;
        }
        //Log.d("URL",url);
        new DownloadNextPage().execute(url);
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        stackOverflowXmlParser = new WireParser();
        entries = null;
        sliderEntries = new ArrayList<>();
        belowEntries = new ArrayList<>();
        List<WireParser.Entry> betweenEntries = new ArrayList<>();
        int count = 0;

        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.page_title)).append("</h3>");
        htmlString.append("<em>").append(getResources().getString(R.string.updated)).append(" ").append(formatter.format(rightNow.getTime())).append("</em>");

        try {
            stream = downloadUrl(urlString);
            //Log.d("Stream",stream+"");
            entries = stackOverflowXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        //Log.d("count", entries.size()+"");

        for (WireParser.Entry entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);

            htmlString.append("'>").append(entry.title).append("</a></p>");
            if (count < 5) {
                count++;

                sliderEntries.add(entry);
            } else {
                if (count < 5) {
                    count++;
                    betweenEntries.add(entry);
                } else {
                    belowEntries.add(entry);
                }
            }

            //Log.d("text",entry.link+" "+entry.title+" "+entry.summary);
            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        return htmlString.toString();
    }

    private String loadXmlForOffset(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        stackOverflowXmlParser = new WireParser();

        int count = 0;

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
            belowEntries.add(belowEntries.size(), entry);

            //Log.d("text",entry.link+" "+entry.title+" "+entry.summary);
            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        stream.close();
        return htmlString.toString();
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        //InputStream stream = ;
        /*if(conn.getInputStream()!=null){
            Log.d("response",conn.getResponseMessage());
            Log.d("code",conn.getResponseCode()+"");
        }*/
        InputStream stream = conn.getInputStream();

        return stream;
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
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "Network problem!", Toast.LENGTH_SHORT).show();
                    }
                });

                // Log.d("Error",e+"");
                return "Exception XML";
            }
        }

        @Override
        protected void onPostExecute(String result) {


            adapter = new HomeAdapter(getContext(), belowEntries, sliderEntries);
            slider.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            //Log.d("modal",modalsList.toString());


            slider.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerViewState = slider.getLayoutManager().onSaveInstanceState();


            /*mViewPager = (ViewPager) view.findViewById(R.id.pager);
            CustomPagerAdapter mCustomPagerAdapter;
            mCustomPagerAdapter = new CustomPagerAdapter(getActivity(),sliderEntries);
            mViewPager.setClipToPadding(true);
            mViewPager.setPadding(16, 10, 16, 0);
            mViewPager.setAdapter(mCustomPagerAdapter);*/



           /* lvTest = (TwoWayView) view.findViewById(R.id.lvItems);
            Twowayadapter twowayadapter = new Twowayadapter(getActivity(),betweenEntries);
            lvTest.setAdapter(twowayadapter);*/
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

            adapter.notifyItemInserted(belowEntries.size());
            adapter.notifyDataSetChanged();
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
