package com.kiros.chatdemo;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Created by Kiros_Wang on 8/28/2016.
 */
public class MyApplication extends Application{
    public static Context applicationContext;
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//初始化
        EMClient.getInstance().init(applicationContext, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
    public static MyApplication getInstance() {
        return instance;
    }
}
