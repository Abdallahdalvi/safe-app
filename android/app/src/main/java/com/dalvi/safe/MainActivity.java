package com.dalvi.safe;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.getcapacitor.BridgeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Let Capacitor initialize the bridge and WebView
        // After super.onCreate, the WebView is created.
        
        setContentView(R.layout.activity_main);
        
        WebView webView = getBridge().getWebView();
        
        // Remove WebView from its current parent and add it to our container
        ViewGroup parent = (ViewGroup) webView.getParent();
        if (parent != null) {
            parent.removeView(webView);
        }
        
        FrameLayout container = findViewById(R.id.webview_container);
        container.addView(webView);

        // Ensure links stay in-app - Bridge should handle this, but we reinforce it
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep everything under your domain in the app
                if (url.contains("safe.dalvi.cloud")) {
                    view.loadUrl(url);
                    return true;
                }
                return false; // Let external links open in browser
            }
        });

        // Setup User Agent for Talk compatibility - Using modern Chrome Mobile UA
        WebSettings settings = webView.getSettings();
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    webView.loadUrl("https://safe.dalvi.cloud/apps/dashboard/");
                    return true;
                } else if (id == R.id.navigation_talk) {
                    webView.loadUrl("https://safe.dalvi.cloud/apps/spreed/");
                    return true;
                } else if (id == R.id.navigation_contacts) {
                    webView.loadUrl("https://safe.dalvi.cloud/apps/contacts/");
                    return true;
                } else if (id == R.id.navigation_profile) {
                    webView.loadUrl("https://safe.dalvi.cloud/settings/user");
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public void onBackPressed() {
        WebView webView = getBridge().getWebView();
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
