package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jake on 7/9/15.
 */
public class SnapzuLogin extends WebViewClient {

    WebView webView;
    Context context;
    String username;
    String password;

    public static final String LOGIN_URL = "http://snapzu.com/login";

    public SnapzuLogin( Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
        webView = new WebView(context);
    }


    public void fetch() {
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(this);
        webView.addJavascriptInterface(this, "SnapzuLogin");

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieSyncManager.sync();


        webView.loadUrl(LOGIN_URL);

    }


    @Override
    public void onPageFinished(WebView view, String url) {

        if (url.equals(LOGIN_URL)) {
            String setUser = String.format("javascript:document.getElementById(\"login\").value = \"%s\";", username);
            String setPassword = String.format("javascript:document.getElementById(\"password\").value = \"%s\";", password);
            view.loadUrl(setUser);
            view.loadUrl(setPassword);
            view.loadUrl("javascript:loginUser();");
        } else {
            view.loadUrl("javascript:window.SnapzuLogin.showCookies (document.cookie);");
        }


    }

    @JavascriptInterface
    public void showCookies(String cookies) {
        MainActivity.bus.post(new LoginPackage(cookies));
    }

    public class LoginPackage {
        public String cookies;
        public LoginPackage(String cookies) {
            this.cookies = cookies;
        }
    }

    public class LoginError {
    }
}
