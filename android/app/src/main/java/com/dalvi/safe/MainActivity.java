package com.dalvi.safe;

import android.os.Bundle;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Explicitly set the content view to use our custom activity_main layout
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        
        // Find the WebView and Bottom Navigation from the layout
        // Note: Capacitor initializes the bridge which sets up the WebView.
        // We ensure we are interacting with the one in our layout.
        WebView webView = findViewById(R.id.webview);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

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
