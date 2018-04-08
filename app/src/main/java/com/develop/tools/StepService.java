package com.develop.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.develop.bean.StepEntity;
import com.develop.sporthealth.HomeStartSport;
import com.develop.sporthealth.MainActivity;
import com.develop.sporthealth.R;
import com.develop.sporthealth.RegisterInfo;
import com.develop.tools.database.SQLOperator;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class StepService extends Service implements SensorEventListener {
    public static final String TAG = "StepService";
    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVER = 1;

    //当前日期
    private static String CURRENT_DATE;
    //当前步数
    private int CURRENT_STEP;
    //传感器
    private SensorManager sensorManager;
    //数据库
    private SQLOperator op;
    //计步传感器类型 0-counter 1-detector
    private static int stepSensor = -1;
    //广播接收
    private BroadcastReceiver mInfoReceiver;
    //发送消息，用来和Service之间传递步数
    private Messenger messenger = new Messenger(new MessengerHandler());
    //是否有当天的记录
    private boolean hasRecord;
    //未记录之前的步数
    private int hasStepCount;
    //下次记录之前的步数
    private int previousStepCount;

    private Notification.Builder builder;

    private NotificationManager notificationManager;
    private Intent nfIntent;

    private SPTools sp;
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            public void run() {
                getStepDetector();
            }
        }).start();
        //startTimeCount();
        sp=new SPTools(StepService.this);
        initTodayData();
        context=StepService.this;

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //定时推送消息
                    String str1=sp.getDate();
                    String str2=TimeTools.getCurTime2();
                    if(sp.getDate().equals(TimeTools.getCurTime2())) {
                        // 设置默认打开的 Activity
                        PushService.setDefaultPushCallback(context, HomeStartSport.class);

                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 这里开启消息推送服务
                        pushQuery.whereEqualTo("installationId", sp.getInstallationId());
                        AVPush.sendMessageInBackground("你今天还没有运动哦，赶紧去运动吧！", pushQuery, new SendCallback() {
                            @Override
                            public void done(AVException e) {

                            }
                        });
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * 此处设将Service为前台，不然当APP结束以后很容易被GC给干掉，这也就是大多数音乐播放器会在状态栏设置一个
         * 原理大都是相通的
         */
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //获取一个Notification构造器
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            builder = new Notification.Builder(this.getApplicationContext(),"sporthealth");
            notificationManager.createNotificationChannel(new NotificationChannel("sporthealth","ces",NotificationManager.IMPORTANCE_HIGH));
        }else {
            builder = new Notification.Builder(this.getApplicationContext());
        }
        /**
         * 设置点击通知栏打开的界面，此处需要注意了，如果你的计步界面不在主界面，则需要判断app是否已经启动，
         * 再来确定跳转页面，这里面太多坑
         */
        nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("今日步数"+CURRENT_STEP+"步") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.logo) // 设置状态栏内的小图标
                .setContentText("加油，要记得勤加运动"); // 设置上下文内容
        // 获取构建好的Notification
        Notification stepNotification = builder.build();

        notificationManager.notify(110,stepNotification);
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, stepNotification);// 开始前台服务

        return START_STICKY;
    }

    /**
     * 自定义handler
     */
    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    try {
                        //这里负责将当前的步数发送出去，可以在界面或者其他地方获取，我这里是在MainActivity中获取来更新界面
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("steps", CURRENT_STEP);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /**
     * 初始化广播
     */
    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);

        mInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    // 屏幕灭屏广播
                    case Intent.ACTION_SCREEN_OFF:
                        //屏幕熄灭
                        break;
                    //关机广播，保存好当前数据
                    case Intent.ACTION_SHUTDOWN:
                        saveStepData();
                        break;
                    // 屏幕解锁广播
                    case Intent.ACTION_USER_PRESENT:
                        break;
                    // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
                    // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
                    // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        saveStepData();
                        break;
                    //监听日期变化
                    case Intent.ACTION_DATE_CHANGED:
                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIME_TICK:
                        saveStepData();
                        isNewDay();
                        break;
                    default:
                        break;
                }
            }
        };
        //注册广播
        registerReceiver(mInfoReceiver, filter);
    }

    /**
     * 初始化当天数据
     */
    private void initTodayData() {
        //获取当前时间
        CURRENT_DATE = TimeTools.getCurrentDate();
        //获取数据库
        op = new SQLOperator(getApplicationContext());
        //获取当天的数据，用于展示
        StepEntity entity = op.getCurDataByDate(CURRENT_DATE,sp.getID());
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        if (entity == null) {
            CURRENT_STEP = 0;
        } else {
            CURRENT_STEP = Integer.parseInt(entity.getSteps());
        }
    }


    /**
     * 监听晚上0点变化初始化数据
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) ||
                !CURRENT_DATE.equals(TimeTools.getCurrentDate())) {
            initTodayData();
        }
    }


    /**
     * 获取传感器实例
     */
    private void getStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        }
    }


    /**
     * 添加传感器监听
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensor = 0;
            sensorManager.registerListener(StepService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            stepSensor = 1;
            sensorManager.registerListener(StepService.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    /**
     * 由传感器记录当前用户运动步数，注意：该传感器只在4.4及以后才有，并且该传感器记录的数据是从设备开机以后不断累加，
     * 只有当用户关机以后，该数据才会清空，所以需要做数据保护
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepSensor == 0) {
            int tempStep = (int) event.values[0];
            if (!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                int thisStepCount = tempStep - hasStepCount;
                CURRENT_STEP += (thisStepCount - previousStepCount);
                previousStepCount = thisStepCount;
            }
        } else if (stepSensor == 1) {
            if (event.values[0] == 1.0) {
                CURRENT_STEP++;
            }
        }
        //实时的存储数据
        //查询数据库中的数据
        StepEntity entity = op.getCurDataByDate(CURRENT_DATE,sp.getID());
        if (entity == null) {
            AVQuery<AVObject> query1 = new AVQuery<>("Step");
            query1.whereEqualTo("UserID", sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("Step");
            query2.whereEqualTo("Date", TimeTools.getCurrentDate());
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list.size() > 0) {
                        for(int i=0;i<list.size();i++) {

                            // 第一参数是 className,第二个参数是 objectId
                            AVObject testObject1 = AVObject.createWithoutData("Step", list.get(i).getObjectId());

                            // 保存到云端
                            testObject1.deleteInBackground();
                        }
                    }
                    saveStepData();
                }
            });
        }else {
            saveStepData();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    /**
     * 保存当天的数据到数据库中，并去刷新通知栏
     */
    private void saveStepData() {
        //推送消息
        if(!sp.getIsSend()&&!sp.getDate().equals("")) {
            int setDay=0;
            int toDay=0;
            String[] setDays=sp.getDate().split(":");
            String[] toDays=TimeTools.getCurTime2().split(":");
            setDay=new Integer(setDays[0])+new Integer(setDays[1]);
            toDay=new Integer(toDays[0])+new Integer(toDays[1]);
            if (toDay>=setDay) {
                sp.setIsSend(true);
                // 设置默认打开的 Activity
                PushService.setDefaultPushCallback(context, HomeStartSport.class);

                AVQuery pushQuery = AVInstallation.getQuery();
                // 这里开启消息推送服务
                pushQuery.whereEqualTo("installationId", sp.getInstallationId());
                AVPush.sendMessageInBackground("你今天还没有运动哦，赶紧去运动吧！", pushQuery, new SendCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
                            sp.setIsSend(true);
                        }else {
                            sp.setIsSend(false);
                        }

                    }
                });
            }
        }
        //查询数据库中的数据
        StepEntity entity = op.getCurDataByDate(CURRENT_DATE,sp.getID());
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        if (entity == null) {
            //这里设置新的一天推送置为false
            sp.setIsSend(false);
            //没有则新建一条数据
            entity = new StepEntity();
            entity.setCurDate(CURRENT_DATE);
            entity.setSteps(String.valueOf(CURRENT_STEP));
            entity.setUserID(sp.getID());

            op.addNewData(entity);

            //这里新增一条数据传到服务器
            AVObject testObject1 = new AVObject("Step");
            testObject1.put("TotalSteps",entity.getSteps());
            testObject1.put("UserID",entity.getUserID());
            testObject1.put("Date",entity.getCurDate());
            testObject1.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                }
            });
        } else {
            //有则更新当前的数据
            entity.setSteps(String.valueOf(CURRENT_STEP));
            entity.setUserID(sp.getID());

            op.updateCurData(entity);

            //这里更新数据传到服务器
            AVQuery<AVObject> query1 = new AVQuery<>("Step");
            query1.whereEqualTo("UserID",entity.getUserID());
            AVQuery<AVObject> query2 = new AVQuery<>("Step");
            query2.whereEqualTo("Date",entity.getCurDate());
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(list.size()>0){
                        // 第一参数是 className,第二个参数是 objectId
                        AVObject testObject1 = AVObject.createWithoutData("Step", list.get(0).getObjectId());

                        testObject1.put("TotalSteps",String.valueOf(CURRENT_STEP));

                        // 保存到云端
                        testObject1.saveInBackground();
                    }
                }
            });
        }

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("今日步数"+CURRENT_STEP+"步") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.logo) // 设置状态栏内的小图标
                .setContentText("加油，要记得勤加运动"); // 设置上下文内容　

        // 获取构建好的Notification
        Notification stepNotification = builder.build();
        //调用更新
        notificationManager.notify(110,stepNotification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //主界面中需要手动调用stop方法service才会结束
        stopForeground(true);
        unregisterReceiver(mInfoReceiver);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}