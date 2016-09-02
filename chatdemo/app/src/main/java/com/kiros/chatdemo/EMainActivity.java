package com.kiros.chatdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseBaseActivity;

import java.util.List;

/**
 * Created by Kiros_Wang on 9/1/2016.
 */
public class EMainActivity extends EaseBaseActivity{
    private EaseConversationListFragment easeConversationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        //make sure activity will not in background if user is logged into another device or removed
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            logout(false,null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        setContentView(R.layout.activity_echat);
        EMOptions options = initChatOptions();
        EaseUI.getInstance().init(this, options);
        easeConversationListFragment = new EaseConversationListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, easeConversationListFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().pushActivity(this);
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d("main", "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d("main", "logout: onSuccess");
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d("main", "logout: onSuccess");
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                //DemoHelper.getInstance().getNotifier().onNewMsg(message);
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            refreshUIWithMessage();
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                //if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (easeConversationListFragment != null) {
                        easeConversationListFragment.refresh();
                    }
                //}
            }
        });
    }

    private EMOptions initChatOptions(){

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        //you need apply & set your own id if you want to use google cloud messaging.
        options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
        options.setHuaweiPushAppId("10492024");

        return options;
    }
}
