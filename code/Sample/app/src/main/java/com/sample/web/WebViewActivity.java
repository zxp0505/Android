package com.sample.web;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sample.BaseActivity;
import com.sample.R;

/**
 * Created by clarkehe on 1/18/16.
 * Todo:
 */
public class WebViewActivity extends BaseActivity {

    private static final String TAG = "WebViewActivity";

    WebView mWebView;

    public WebViewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Let's display the progress in the activity title bar, like the
        // browser app does.
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.webview_activity);

        mWebView = (WebView)findViewById(R.id.webView);
        final WebView webView = mWebView;

        // 不设置，loadUrl会启动浏览器
        mWebView.setWebViewClient(new MyWebViewClient());

        //
        mWebView.setWebChromeClient(new MyWebChromeClient());

        // JS
        mWebView.getSettings().setJavaScriptEnabled(true);

//        mWebView.loadUrl("http://baidu.com");
        mWebView.loadUrl("file:///android_asset/www/index.html");

        //Load JS after Page Loaded
        //NATIVE CALL JS
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //. NATIVE CALL JS, 没有返回值。
                //webView.loadUrl("javascript:jsFun('Hello World!')");
                //webView.evaluateJavascript();
            }
        }, 1000);

          //1. JS CALL NATIVE. 不安全，有返回值。
//        webView.addJavascriptInterface(new JsObject(), "injectedObject");
//        webView.loadData("<!DOCTYPE html><title></title>", "text/html", null);
//        webView.loadUrl("javascript:alert(injectedObject.toString())");
    }

//    class JsObject {
//        @JavascriptInterface
//        public String toString() { return "injectedObject"; }
//    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewClient extends WebViewClient
    {
        //JSBridge 2. JS CALL NATIVE，没有返回值
        //2. WX,使用的方式是在JS中存一个回CALLBACK, NATIVE来调用。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading, url:" + url);
            if (url.contains("jsbridge:")){
                return true;
            }
            return false;
        }

        //Load Native Cache Res
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.d(TAG, "shouldInterceptRequest, url:" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    class MyWebChromeClient extends  WebChromeClient
    {
        // 3. JS CALL NATIVE, 有返回值。
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

            Log.d(TAG, "onJsPrompt, url:" + url);
            Log.d(TAG, "onJsPrompt, message:" + message);
            Log.d(TAG, "onJsPrompt, defaultValue:" + defaultValue);

            if (message.contains("jsbridge:")){
                result.confirm("return");
                return true;
            }

            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }
}
