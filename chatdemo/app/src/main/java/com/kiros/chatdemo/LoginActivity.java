package com.kiros.chatdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Kiros_Wang on 8/29/2016.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (EMClient.getInstance().isLoggedInBefore()) {
            startActivity(new Intent(LoginActivity.this, EMainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        login();
    }

    private void login() {
        EMClient.getInstance().login("a", "1", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                startActivity(new Intent(LoginActivity.this, EMainActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }
}
