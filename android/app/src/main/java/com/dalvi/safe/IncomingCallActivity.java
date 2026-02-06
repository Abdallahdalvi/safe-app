package com.dalvi.safe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class IncomingCallActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Show over lockscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.activity_incoming_call);
        
        String callerName = getIntent().getStringExtra("CALLER_NAME");
        if (callerName != null) {
            TextView nameView = findViewById(R.id.caller_name);
            nameView.setText(callerName);
        }
        
        findViewById(R.id.btn_answer).setOnClickListener(v -> answerCall());
        findViewById(R.id.btn_decline).setOnClickListener(v -> declineCall());
    }
    
    private void answerCall() {
        // Logic to communicate back to WebView to answer
        finish();
    }
    
    private void declineCall() {
        // Logic to communicate back to WebView to decline
        finish();
    }
}
