package org.stuartresearch.snapzuisfunner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.otto.Subscribe;

import org.stuartresearch.SnapzuAPI.Post;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PostActivity extends AppCompatActivity implements View.OnTouchListener {

    @Bind(R.id.post_webview) WebView mWebView;
    @Bind(R.id.post_toolbar) Toolbar toolbar;

    Post post;

    SlidrInterface slidrInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        MainActivity.bus.register(this);

        Intent received = getIntent();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // slide in
        overridePendingTransition(R.anim.slide_left, 0);

        // Sliding mechanism
        SlidrConfig config = new SlidrConfig.Builder().sensitivity(0.5f).build();
        slidrInterface = Slidr.attach(this, config);

        // Butterknife does not work with webview?
        mWebView = (WebView) findViewById(R.id.post_webview);

        // Configure webview
        mWebView.setOnTouchListener(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(new CustomWebViewClient());

        // Load website
        mWebView.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.webview_back:
                mWebView.goBack();
                break;
            case R.id.webview_refresh:
                mWebView.reload();
                break;
            case R.id.webview_forward:
                mWebView.goForward();
                break;
            case R.id.webview_open_in_browser:
                Intent openInBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebView.getUrl()));
                startActivity(openInBrowser);
                break;
            case R.id.webview_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, post.getTitle()
                );
                share.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                startActivity(Intent.createChooser(share, "Share Current Page"));
                break;
            case R.id.webview_fullscreen:
                Toast.makeText(this, "Fullscreen is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.webview_more:
                Toast.makeText(this, "More is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.bus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_right);
    }

    // WEBVIEW TOUCHED
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Do not steal scrolling of mWebView
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    // Sent from MainActivity
    @Subscribe
    public void onPostReceive(MainActivity.SinglePostPackage singlePostPackage) {
        this.post = singlePostPackage.post;
    }

    private static class CustomWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //do whatever you want with the url that is clicked inside the webview.
            //for example tell the webview to load that url.
            view.loadUrl(url);
            //return true if this method handled the link event
            //or false otherwise
            return true;
        }
    }

}
