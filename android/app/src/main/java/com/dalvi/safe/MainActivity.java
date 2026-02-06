package com.dalvi.safe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
        
        WebView primaryWebView = getBridge() != null ? getBridge().getWebView() : null;
        if (primaryWebView != null) {
            // Remove WebView from its current parent and add it to our container
            ViewGroup parent = (ViewGroup) primaryWebView.getParent();
            if (parent != null) {
                parent.removeView(primaryWebView);
            }
            
            FrameLayout container = findViewById(R.id.webview_container);
            if (container != null) {
                container.addView(primaryWebView);
                primaryWebView.getSettings().setUserAgentString(userAgent);
                // Store primary WebView (Home tab)
                webViews.put(R.id.navigation_home, primaryWebView);
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == currentTabId) return true;
                
                switchTab(id);
                return true;
            });
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] permissions;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO,
                        android.Manifest.permission.READ_MEDIA_AUDIO,
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS
                };
            } else {
                permissions = new String[]{
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS
                };
            }
            requestPermissions(permissions, 101);
        }

        // Configure primary WebView
        configureWebView(primaryWebView);

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
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidBridge");
        
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onPermissionRequest(final android.webkit.PermissionRequest request) {
                request.grant(request.getResources());
            }

            @Override
            public boolean onConsoleMessage(android.webkit.ConsoleMessage consoleMessage) {
                android.util.Log.d("SafeAppJS", consoleMessage.message());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject script to catch notifications and rings
                view.evaluateJavascript(
                    "(function() { " +
                    "  if (window.SafeAppBridgeInjected) return; " +
                    "  console.log('SafeApp: Injecting Bridge...'); " +
                    "  const OriginalNotification = window.Notification; " +
                    "  window.Notification = function(title, options) { " +
                    "    console.log('SafeApp: Notification caught:', title, options); " +
                    "    AndroidBridge.showNotification(title, options.body || ''); " +
                    "    return new OriginalNotification(title, options); " +
                    "  }; " +
                    "  Object.assign(window.Notification, OriginalNotification); " +
                    "  window.Notification.permission = 'granted'; " +
                    "  window.Notification.requestPermission = function(cb) { " +
                    "    if(cb) cb('granted'); return Promise.resolve('granted'); " +
                    "  }; " +
                    "  /* Heartbeat to keep WebView from sleeping */ " +
                    "  setInterval(function() { " +
                    "    console.log('SafeApp: Heartbeat'); " +
                    "    window.dispatchEvent(new Event('mousemove')); " +
                    "  }, 5000); " +
                    "  window.SafeAppBridgeInjected = true; " +
                    "  console.log('SafeApp: Bridge Injected Successfully'); " +
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

    private static class WebAppInterface {
        private android.content.Context mContext;
        WebAppInterface(android.content.Context c) {
            mContext = c;
        }

        @android.webkit.JavascriptInterface
        public void showNotification(String title, String body) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(MainActivity.NOTIFICATION_SERVICE);
            String channelId = "SafeAppWebNotifications";
            String groupKey = "com.dalvi.safe.MESSAGES_" + title.replaceAll("[^a-zA-Z0-9]", "");
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Web Notifications", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            boolean isCall = title.toLowerCase().contains("call") || 
                            body.toLowerCase().contains("incoming call") || 
                            body.toLowerCase().contains("talk with you") ||
                            body.toLowerCase().contains("invite");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(isCall ? NotificationCompat.CATEGORY_CALL : NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setGroup(groupKey);

            if (isCall) {
                Intent fullScreenIntent = new Intent(mContext, IncomingCallActivity.class);
                fullScreenIntent.putExtra("CALLER_NAME", body.replace(" would like to talk with you", ""));
                fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                
                PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(mContext, 0,
                        fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                
                builder.setFullScreenIntent(fullScreenPendingIntent, true)
                       .setOngoing(true);
                       
                mContext.startActivity(fullScreenIntent);
            }

            // Use a stable ID for the same user/title to trigger grouping/updates correctly
            int notificationId = Math.abs(title.hashCode());
            notificationManager.notify(notificationId, builder.build());
            
            // Send a Summary notification for the group to ensure proper collapsing
            Notification summaryNotification = new NotificationCompat.Builder(mContext, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setGroup(groupKey)
                    .setGroupSummary(true)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(Math.abs(groupKey.hashCode()), summaryNotification);
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
