package com.dalvi.safe;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        
        WebView webView = findViewById(R.id.webview);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (webView != null) {
            // Fix "Browser not supported" error by setting a modern User Agent
            WebSettings settings = webView.getSettings();
            String originalUserAgent = settings.getUserAgentString();
            // Append a standard Chrome/Safari string to trick Nextcloud Talk into supporting the WebView
            settings.setUserAgentString(originalUserAgent + " Mobile Safari/537.36 (SafeApp; Android)");
        }

        if (bottomNav != null && webView != null) {
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
        WebView webView = findViewById(R.id.webview);
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
