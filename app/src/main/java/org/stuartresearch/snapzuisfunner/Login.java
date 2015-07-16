package org.stuartresearch.snapzuisfunner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.parceler.Parcels;
import org.stuartresearch.SnapzuAPI.Soup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Login extends ActionBarActivity {

    @Bind(R.id.login_webview) WebView webView;
    public static final String LOGIN_URL = "http://snapzu.com/login";

    Pattern findProfile = Pattern.compile("(profile=)(\\w+)");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new LoginWebClient());

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();


        webView.addJavascriptInterface(this, "SnapzuLogin");
        webView.loadUrl(LOGIN_URL);

        MainActivity.bus.register(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.bus.unregister(this);
    }


    private static class LoginWebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.equals(LOGIN_URL)) {
                view.setEnabled(false);
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url){
                super.onPageFinished(view, url);
            if (!url.equals(LOGIN_URL)) {
                view.loadUrl("javascript:window.SnapzuLogin.getData (document.cookie, document.documentElement.innerHTML);");
            }
        }


    }

    @JavascriptInterface
    public void getData(String cookies, String html) {
        makeProfile(cookies, html);
    }


    public void makeProfile(String cookies, String html) {
        if (cookies.split(";").length >= 7) {
            Matcher matcher = findProfile.matcher(cookies);
            if (matcher.find()) {
                String username = matcher.group(2);
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                try {
                    Soup soup = new Soup();
                    String imageUrl = soup.extractPicture(html);

                    Profile profile = new Profile(username, cookies, imageUrl);
                    Intent result = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("profile", Parcels.wrap(profile));
                    result.putExtras(bundle);
                    setResult(RESULT_OK, result);
                    finish();
                    return;
                } catch (Exception dropIt) {}
            }
        }

            Toast.makeText(this, "Login failed. Did you select remember me?", Toast.LENGTH_LONG).show();
            finish();
    }

}
