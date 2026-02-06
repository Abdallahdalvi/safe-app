package com.dalvi.safe;

import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.os.Bundle;

public class SafeCallService extends ConnectionService {
    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        SafeConnection connection = new SafeConnection();
        connection.setInitializing();
        
        Bundle extras = request.getExtras();
        String callerName = extras.getString(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS);
        
        // Show our custom incoming call UI
        android.content.Intent intent = new android.content.Intent(this, IncomingCallActivity.class);
        intent.putExtra("CALLER_NAME", callerName);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        
        return connection;
    }

    private static class SafeConnection extends Connection {
        public SafeConnection() {
            setConnectionProperties(PROPERTY_SELF_MANAGED);
            setAudioModeIsVoip(true);
        }

        @Override
        public void onAnswer() {
            super.onAnswer();
            setActive();
            // Communicate to App to start WebRTC
        }

        @Override
        public void onDisconnect() {
            super.onDisconnect();
            setDisconnected(new android.telecom.DisconnectCause(android.telecom.DisconnectCause.LOCAL));
            destroy();
        }
    }
}
