package com.develop.sporthealth;

import android.app.Application;
import android.os.Build;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.develop.tools.singtools.ResolutionUtil;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //签到所需的初始化语句
        ResolutionUtil.getInstance().init(this);

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
        // 启用北美节点, 需要在 initialize 之前调用
        //AVOSCloud.useAVCloudUS();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            PushService.setDefaultChannelId(this, "public");
        }
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"jTA44pykLbSjHhNSch2vfrIU-gzGzoHsz","ixoFaSyVCq4FL9vNLSCOmCvb");
    }
}
