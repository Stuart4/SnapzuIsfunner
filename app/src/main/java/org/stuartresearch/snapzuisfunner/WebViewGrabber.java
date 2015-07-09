package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jake on 7/8/15.
 */
public class WebViewGrabber extends WebViewClient {

    WebView webView;
    Context context;
    String baseAddress;
    String address;
    String cookies;

    public WebViewGrabber(Context context, String baseAddress, String address, String cookies) {
        webView = new WebView(context);
        this.context = context;
        this.baseAddress = baseAddress;
        this.address = address;
        this.cookies = cookies;
    }

    public void fetch() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "WebViewGrabber");

        webView.setWebViewClient(this);

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(baseAddress, cookies);
        cookieSyncManager.sync();


        webView.loadUrl(address);

    }

    @JavascriptInterface
    public void showHTML(String html) {
        MainActivity.bus.post(new HTMLPackage(html));
    }

    @JavascriptInterface
    public void showCookies(String cookies) {
        MainActivity.bus.post(new CookiePackage(cookies));
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        //Load HTML
        view.loadUrl("javascript:window.WebViewGrabber.showHTML ('<head>' + document.getElementsByTagName('html')[0].innerHTML + '</head>');");
        view.loadUrl("javascript:window.WebViewGrabber.showCookies (document.cookie);");


    }

    public class CookiePackage {
        public String cookies;
        public CookiePackage(String cookies) {
            this.cookies = cookies;
        }
    }

    public class HTMLPackage {
        public String html;
        public HTMLPackage(String html) {
            this.html = html;
        }
    }
}

