package com.spriv.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.spriv.R;

public class VisitWebActivity extends AppCompatActivity {
    public final static String PRIVACY_POLICY = "privacy_policy.html";
    public final static String LEGAL_NOTICE = "legal_notice.html";
    public final static String TERMS_OF_USE = "terms_of_use.html";
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        getSupportActionBar().setTitle(title);
        setContentView(R.layout.activity_visit_web);
        webView = (WebView) findViewById(R.id.webView);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        Intent intent = getIntent();
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
            }
        });
        webView.loadUrl("file:///android_asset/" + url);
        // progress.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }
}

