package com.kiros.chatdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EasyUtils;

/**
 * chat activity，EaseChatFragment was used {@link #}
 *
 */
public class ChatActivity extends Activity {
    public static ChatActivity activityInstance;
    String toChatUsername;
    private EditText send_content;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        send_content = (EditText) findViewById(R.id.send_content);
        btn_send = (Button) findViewById(R.id.btn_send);
        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = send_content.getText().toString().trim();
                EMMessage message = EMMessage.createTxtSendMessage(msg, toChatUsername);
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	// make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
    
    @Override
    public void onBackPressed() {
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }
}
