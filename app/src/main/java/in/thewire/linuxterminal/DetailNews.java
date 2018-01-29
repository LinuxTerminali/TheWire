package in.thewire.linuxterminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class DetailNews extends AppCompatActivity {
    private String link = "http://thewire.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Read News");
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        String title = intent.getStringExtra("title");
        String pubdate = intent.getStringExtra("pubdate");
        String author = intent.getStringExtra("author");
        link = intent.getStringExtra("link");
        //String authorurl = author.replaceAll(" ", "_").toLowerCase();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        content = content.replaceAll("[<](/)?div[^>]*[>]", "");
        Log.d("detail" + width, content);

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>The Wire</title>" +
                "<style>img{display: block; height: auto; width:auto; max-width: 100%;}" +
                "p {color :#212121;text-align: justify;text-justify: inter-word;font-family: 'Roboto';" +
                "font-weight: 400;display: block;max-width:100%}" +
                "iframe {display: block;\n max-width:100%;\n height:70%;margin-top:10px;\n " +
                "margin-bottom:10px;\n}</style>" +
                "</head>" +
                "<body ><h2 color :#212121; font-family: " +
                "'Roboto';font-weight: 400;display: block;max-width:100%>" + title + "</h2>" +
                "<h6>BY " + author + " ON " + pubdate + "</h6>" +

                content +
                "</body>" +
                "</html>";

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.loadData(html, "text/html;charset=utf-8", null);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        /*settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);*/
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sharemenu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            // Log.d("share", "Share Action Provider is null");
        }
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);

        return shareIntent;
    }
}
