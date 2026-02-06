package com.dalvi.safe;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("SafeApp", "From: " + remoteMessage.getFrom());
        
        // Handle data payloads from Nextcloud
        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("type");
            if ("call".equals(type)) {
                // Trigger native call UI
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("SafeApp", "Refreshed token: " + token);
        // Upload token to Nextcloud server
    }
}
