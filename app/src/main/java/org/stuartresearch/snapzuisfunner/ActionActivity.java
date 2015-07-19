package org.stuartresearch.snapzuisfunner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActionActivity extends ActionBarActivity implements View.OnTouchListener{

    @Bind(R.id.post_webview) WebView mWebView;
    @Bind(R.id.post_toolbar) Toolbar toolbar;

    MenuItem arrowBackUp;
    MenuItem arrowForwardDown;
    MenuItem openInBrowserCompose;
    MenuItem fullscreenSearch;
    MenuItem readerSort;

    SlidrInterface slidrInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Sliding mechanism
        SlidrConfig config = new SlidrConfig.Builder().sensitivity(0.5f).build();
        slidrInterface = Slidr.attach(this, config);


        mWebView = (WebView) findViewById(R.id.post_webview);


        // Configure webview
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebViewClient(new ActionWebClient());

        mWebView.setOnTouchListener(this);

        Intent intent = getIntent();


        // Get the right cookies in there
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.setCookie(MainActivity.address, intent.getStringExtra("cookies"));

        // Load website
        mWebView.loadUrl(intent.getStringExtra("url"));

        overridePendingTransition(R.anim.slide_left, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);

        arrowBackUp = menu.findItem(R.id.post_1);
        arrowForwardDown = menu.findItem(R.id.post_3);
        openInBrowserCompose = menu.findItem(R.id.post_4);
        fullscreenSearch = menu.findItem(R.id.post_5);
        readerSort = menu.findItem(R.id.post_6);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_right);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.post_1:
                mWebView.goBack();
                break;
            case R.id.post_2:
                mWebView.reload();
                break;
            case R.id.post_3:
                mWebView.goForward();
                break;
            case R.id.post_4:
                Intent openInBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebView.getUrl()));
                startActivity(openInBrowser);
                break;
            case R.id.post_5:
                Toast.makeText(this, "Fullscreen is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.post_6:
                Toast.makeText(this, "Reader is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.post_7:
                Toast.makeText(this, "More is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Do not steal scrolling of mWebView
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    private static class ActionWebClient extends WebViewClient {

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
