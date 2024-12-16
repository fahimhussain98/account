package wts.com.accountpe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wts.com.accountpe.R;

public class MyWebViewActivity extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);

        url = getIntent().getStringExtra("url");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //webView.setWebViewClient(new mywebview(MyWebViewActivity.this));

        webView.addJavascriptInterface(new WebviewInterface(), "Interface");

        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100 && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        webView.loadUrl(url);

        progressDialog = new ProgressDialog(MyWebViewActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Connect to internet");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }

    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class mywebview extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Toast.makeText(MainActivity.this, "In On Page Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Toast.makeText(MainActivity.this, "In on page finished", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }
        public mywebview(MyWebViewActivity mainActivity) {
        }
    }

    public class WebviewInterface {
        @JavascriptInterface
        public void paymentResponse(String client_txn_id, String txn_id) {
            Log.i("clientTxnId", client_txn_id);
            Log.i("txnId", txn_id);
            // this function is called when payment is done (success, scanning ,timeout or cancel by user).
            // You must call the check order status API in server and get update about payment.
            // 🚫 Do not Call UpiGateway API in Android App Directly.
            //    Toast.makeText(MyWebViewActivity.this, "Order ID: "+client_txn_id+", Txn ID: "+txn_id, Toast.LENGTH_SHORT).show();
            finish();
            // Close the Webview.
        }

        @JavascriptInterface
        public void errorResponse() {
            // this function is called when Transaction in Already Done or Any other Issue.
            //     Toast.makeText(MyWebViewActivity.this, "Transaction Error.", Toast.LENGTH_SHORT).show();
            // Close the Webview.
            finish();
        }
    }

}