package com.kiros.chatdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private ListView list;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "连接到聊天服务器", Toast.LENGTH_LONG).show();
                    conversationList.addAll(loadConversationList());
                        list.setAdapter(new EaseConversationAdapater(MainActivity.this, 0, conversationList));
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "消息", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.chat_list);
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

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("userId", conversationList.get(pos).getUserName()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    private void logout() {
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("main", "退出聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            Message message=new Message();
            message.what=2;
            mHandler.sendMessage(message);
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        Toast.makeText(MainActivity.this, "显示帐号已经被移除", Toast.LENGTH_LONG).show();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(MainActivity.this, "显示帐号在其他设备登录", Toast.LENGTH_LONG).show();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this))
                        //连接不到聊天服务器
                        Toast.makeText(MainActivity.this, "连接不到聊天服务器", Toast.LENGTH_LONG).show();
                        else
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(MainActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            Message message=new Message();
            message.what=3;
            mHandler.sendMessage(message);
            //Toast.makeText(MainActivity.this, messages.get(0).toString(), Toast.LENGTH_LONG).show();
            messages.get(0).setMessageStatusCallback(new EMCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "message", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            Toast.makeText(MainActivity.this, "message1", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
            Toast.makeText(MainActivity.this, "message2", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
            Toast.makeText(MainActivity.this, "message3", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            Toast.makeText(MainActivity.this, "message4", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
                        String st10 = "已经被移除";
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                    Toast.makeText(MainActivity.this, username, Toast.LENGTH_LONG).show();
                }
            });
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onContactAgreed(String username) {}
        @Override
        public void onContactRefused(String username) {}
    }
}
