package com.dalvi.safe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.BridgeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BridgeActivity {
    private java.util.Map<Integer, WebView> webViews = new java.util.HashMap<>();
    private int currentTabId = R.id.navigation_home;
    private String userAgent = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        WebView primaryWebView = getBridge().getWebView();
        
        // Remove WebView from its current parent and add it to our container
        ViewGroup parent = (ViewGroup) primaryWebView.getParent();
        if (parent != null) {
            parent.removeView(primaryWebView);
        }
        
        FrameLayout container = findViewById(R.id.webview_container);
        container.addView(primaryWebView);
        primaryWebView.getSettings().setUserAgentString(userAgent);

        // Store primary WebView (Home tab)
        webViews.put(R.id.navigation_home, primaryWebView);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == currentTabId) return true;
                
                switchTab(id);
                return true;
            });
        }

        // Start persistence service
        Intent serviceIntent = new Intent(this, AppKeepAliveService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void switchTab(int id) {
        WebView nextWebView = webViews.get(id);
        WebView currentWebView = webViews.get(currentTabId);

        if (currentWebView != null) {
            currentWebView.setVisibility(android.view.View.GONE);
        }

        if (nextWebView == null) {
            // Create new WebView for this tab
            nextWebView = new WebView(this);
            nextWebView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.MATCH_PARENT
            ));
            
            configureWebView(nextWebView);
            
            FrameLayout container = findViewById(R.id.webview_container);
            container.addView(nextWebView);
            
            String url = getUrlForId(id);
            nextWebView.loadUrl(url);
            
            webViews.put(id, nextWebView);
        }

        nextWebView.setVisibility(android.view.View.VISIBLE);
        currentTabId = id;
    }

    private void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setUserAgentString(userAgent);
        
        // Add native bridge
        webView.addJavascriptInterface(new WebAppInterface(), "AndroidBridge");
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject script to catch notifications and rings
                view.evaluateJavascript(
                    "(function() { " +
                    "  const OriginalNotification = window.Notification; " +
                    "  window.Notification = function(title, options) { " +
                    "    AndroidBridge.showNotification(title, options.body || ''); " +
                    "    return new OriginalNotification(title, options); " +
                    "  }; " +
                    "  window.Notification.permission = 'granted'; " +
                    "  window.Notification.requestPermission = function(cb) { " +
                    "    if(cb) cb('granted'); return Promise.resolve('granted'); " +
                    "  }; " +
                    "  /* Catch Nextcloud Talk specific ring events if possible */ " +
                    "  console.log('SafeApp: Bridge Injected'); " +
                    "})();", null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("safe.dalvi.cloud")) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });
    }

    public class WebAppInterface {
        @android.webkit.JavascriptInterface
        public void showNotification(String title, String body) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channelId = "SafeAppWebNotifications";
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Web Notifications", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(MainActivity.this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            notificationManager.notify((int) System.currentTimeMillis(), notification);
            
            // If it's a call, trigger the call UI
            if (title.toLowerCase().contains("call") || body.toLowerCase().contains("incoming call")) {
                Intent intent = new Intent(MainActivity.this, IncomingCallActivity.class);
                intent.putExtra("CALLER_NAME", title);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private String getUrlForId(int id) {
        if (id == R.id.navigation_home) return "https://safe.dalvi.cloud/apps/dashboard/";
        if (id == R.id.navigation_talk) return "https://safe.dalvi.cloud/apps/spreed/";
        if (id == R.id.navigation_contacts) return "https://safe.dalvi.cloud/apps/contacts/";
        if (id == R.id.navigation_profile) return "https://safe.dalvi.cloud/settings/user";
        return "https://safe.dalvi.cloud/";
    }

    @Override
    public void onBackPressed() {
        WebView webView = webViews.get(currentTabId);
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
