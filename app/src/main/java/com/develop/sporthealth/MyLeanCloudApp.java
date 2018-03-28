package com.develop.sporthealth;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
        // 启用北美节点, 需要在 initialize 之前调用
        //AVOSCloud.useAVCloudUS();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"jTA44pykLbSjHhNSch2vfrIU-gzGzoHsz","ixoFaSyVCq4FL9vNLSCOmCvb");
    }
}
