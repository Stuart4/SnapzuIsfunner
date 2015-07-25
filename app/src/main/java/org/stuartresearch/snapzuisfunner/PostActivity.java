package org.stuartresearch.snapzuisfunner;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.parceler.Parcel;
import org.parceler.Parcels;
import org.stuartresearch.SnapzuAPI.Comment;
import org.stuartresearch.SnapzuAPI.Post;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class PostActivity extends AppCompatActivity implements View.OnTouchListener, SlidingUpPanelLayout.PanelSlideListener {

    public static final String READABILITY = "javascript:(%0A%28function%28%29%7Bwindow.baseUrl%3D%27//www.readability.com%27%3Bwindow.readabilityToken%3D%27%27%3Bvar%20s%3Ddocument.createElement%28%27script%27%29%3Bs.setAttribute%28%27type%27%2C%27text/javascript%27%29%3Bs.setAttribute%28%27charset%27%2C%27UTF-8%27%29%3Bs.setAttribute%28%27src%27%2CbaseUrl%2B%27/bookmarklet/read.js%27%29%3Bdocument.documentElement.appendChild%28s%29%3B%7D%29%28%29)";

    @Bind(R.id.post_webview) WebView mWebView;
    @Bind(R.id.post_toolbar) Toolbar toolbar;
    @Bind(R.id.comment_container) FrameLayout commentContainer;
    @Bind(R.id.sliding_layout) SlidingUpPanelLayout slidingUpPanelLayout;

    Post post;
    Comment[] comments;

    MenuItem arrowBackUp;
    MenuItem arrowForwardDown;
    MenuItem openInBrowserCompose;
    MenuItem fullscreenSearch;
    MenuItem readerSort;

    boolean showingComments = false;

    String url;
    String cookies;

    boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        MainActivity.bus.register(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // slide in
        overridePendingTransition(R.anim.slide_left, 0);

        slidingUpPanelLayout.setPanelSlideListener(this);

        // Butterknife does not work with webview?
        mWebView = (WebView) findViewById(R.id.post_webview);

        // Prevent sliding
        mWebView.setOnTouchListener(this);
        commentContainer.setOnTouchListener(this);

        // Configure webview
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebViewClient(new PostWebClient());

        Bundle extras = getIntent().getExtras();

        cookies = extras.getString("cookies", "");
        url = extras.getString("url", "");

        // Get the right cookies in there
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.setCookie(MainActivity.address, cookies);


        // Load website
        mWebView.loadUrl(url);
        post = ((SinglePost) Parcels.unwrap(extras.getParcelable("post"))).post;


        new PopulateComments(post.getCommentsLink()).execute();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (showingComments) {
            onCommentsItemSelected(id);
        } else {
            onWebviewItemSelected(id);
        }

        return super.onOptionsItemSelected(item);
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

    public void onWebviewItemSelected(int id) {
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
                toggleFullScreen();
                break;
            case R.id.post_6:
                mWebView.loadUrl(READABILITY);
                break;
            case R.id.post_7:
                Toast.makeText(this, "More is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void onCommentsItemSelected(int id) {
        switch (id) {
            case R.id.post_1:
                openComments();
                break;
            case R.id.post_2:
                new PopulateComments(post.getCommentsLink()).execute();
                break;
            case R.id.post_3:
                openComments();
                break;
            case R.id.post_4:
                openComments();
                break;
            case R.id.post_5:
                presentSearchDialog();
                break;
            case R.id.post_6:
                Toast.makeText(this, "Sort is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.post_7:
                Toast.makeText(this, "More is not implemented.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void openComments() {
        if (post == null) {
            return;
        }
        Intent intent = new Intent(this, ActionActivity.class);
        intent.putExtra("url", post.getCommentsLink());
        intent.putExtra("cookies", cookies);
        startActivity(intent);
    }

    public void sharePage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, post.getTitle()
        );
        share.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
        startActivity(Intent.createChooser(share, "Share Current Page"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.bus.unregister(this);
        mWebView.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_right);
    }

    // WEBVIEW or LISTVIEW TOUCHED
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Do not steal scrolling of mWebView
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @Subscribe
    public void onCommentsReceive(PopulateComments.CommentsPackage commentsPackage) {
        this.comments = commentsPackage.comments;

        //Treeview
        presentComments();
    }

    @Subscribe
    public void onCommentsError(PopulateComments.CommentsError commentsError) {
        Toast.makeText(this, "Network error is not implemented", Toast.LENGTH_SHORT).show();
    }

    private static class PostWebClient extends WebViewClient {

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


    public void toggleFullScreen() {
        if (isFullScreen) {
            presentNotfullScreen();
        } else {
            presentFullScreen();
        }
    }

    public void presentFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        slidingUpPanelLayout.setEnabled(false);

        isFullScreen = true;
    }

    public void presentNotfullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        slidingUpPanelLayout.setEnabled(true);

        isFullScreen = false;
    }

    @Override
    public void onPanelCollapsed(View view) {
        showingComments = false;
        mWebView.onResume();
        arrowBackUp.setIcon(R.drawable.ic_arrow_back_black_24dp);
        arrowBackUp.setTitle("Back");
        arrowForwardDown.setIcon(R.drawable.ic_arrow_forward_black_24dp);
        arrowBackUp.setTitle("Forward");
        openInBrowserCompose.setIcon(R.drawable.ic_open_in_browser_black_24dp);
        arrowBackUp.setTitle("Open In Browser");
        fullscreenSearch.setIcon(R.drawable.ic_fullscreen_black_24dp);
        arrowBackUp.setTitle("Fullscreen");
        readerSort.setIcon(R.drawable.ic_readability_black_24dp);
        arrowBackUp.setTitle("Reader Mode");
    }

    @Override
    public void onPanelExpanded(View view) {
        showingComments = true;
        mWebView.onPause();
        arrowBackUp.setIcon(R.drawable.ic_arrow_up_black_24dp);
        arrowBackUp.setTitle("Up Vote");
        arrowForwardDown.setIcon(R.drawable.ic_arrow_down_black_24dp);
        arrowBackUp.setTitle("Down Vote");
        openInBrowserCompose.setIcon(R.drawable.ic_create_black_24dp);
        arrowBackUp.setTitle("Compose");
        fullscreenSearch.setIcon(R.drawable.ic_search_black_24dp);
        arrowBackUp.setTitle("Search");
        readerSort.setIcon(R.drawable.ic_sort_black_24dp);
        arrowBackUp.setTitle("Sort");

        if (isFullScreen) {
            presentNotfullScreen();
        }
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    public void presentSearchDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_comment_search, null);
        final TextView searchText = (TextView) view.findViewById(R.id.comment_search_text);

        builder.setView(view);

        builder.setTitle("Find In Comments");

        builder.setPositiveButton("SEARCH", (dialog, which) -> {
            onSearchTerm(searchText.getText().toString());
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    public void onSearchTerm(String term) {
        Observable ob = SearchComments.findInComments(comments, term.split(" "));
        ob = ob.subscribeOn(Schedulers.newThread());
        ob = ob.observeOn(AndroidSchedulers.mainThread());
        ob.subscribe((Action1) (Object o) -> {
            Toast.makeText(getApplicationContext(), Integer.toString((int) o), Toast.LENGTH_LONG).show();
        });
    }

    @Parcel
    public static class SinglePost {
        Post post;

        public SinglePost() {}

        public SinglePost(Post post) {
            this.post = post;
        }
    }

    public void presentComments() {
        AndroidTreeView treeView = TreeViewConfiguration.buildTreeView(this, post, comments);
        commentContainer.addView(treeView.getView());
        treeView.expandAll();
    }
}
