package com.develop.sporthealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/8.
 */

public class HomeStartSport extends AppCompatActivity implements AMapLocationListener,LocationSource,View.OnClickListener{

    private RelativeLayout title;
    private MapView map;
    private MyLayout back;
    //一开始进去的布局
    private RelativeLayout visible;
    private Button start;
    private MyLayout plan;
    //操作按钮显示信息的布局
    private RelativeLayout visible2;
    private ImageView slide;
    private LinearLayout ly1;
    private TextView km;
    private TextView time;
    private TextView speed;
    private TextView hot;
    private LinearLayout ly2;
    private TextView km2;
    private TextView time2;
    private LinearLayout ly3;
    private Button play;
    private Button pause;
    private Button stop;

    private Button share;
    //是否详细界面
    private boolean isShow=true;
    //是否开始跑步
    private boolean isStart=false;
    //是否初始化完成
    private boolean isFinish=false;

    private double mTime=0;
    private String SportID="";
    private String RunID="";
    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat db = new DecimalFormat("#.##");
    private DecimalFormat db2 = new DecimalFormat("#");
    private String startTime="";
    private String endTime="";

    private SPTools sp;
    private SQLOperator op;
    private Context context;

    //声明AMapLocationClient对象
    private AMapLocationClient mapClient;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mapOption=null;
    //声明地图控制器
    private AMap aMap;
    //声明定位蓝点
    private MyLocationStyle style;
    //监听对象
    private OnLocationChangedListener mListener;
    //定义地图画线条
    private Polyline polyline;
    private PolylineOptions polylineOptions;
    //定义地图点
    private List<LatLng> latLngs;
    //定义地图图层
    private Marker marker;
    private List<Marker> markers;
    private MarkerOptions markerOptions;

    //是第一次运行标志
    private boolean isFirst=true;
    //
    private LatLng latLng1=null;
    private LatLng latLng2=null;
    //当前速度
    private int tspeed=0;
    //总距离
    private double distance=0.00;
    //总时间
    private int totalTime=0;
    //总热量
    private double totalHot=0;
    //时、分、秒
    private int HMS=0;
    private int H=0;
    private int M=0;
    private int S=0;


    private PowerManager.WakeLock wakeLock;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    km.setText(db.format(distance));
                    km2.setText(db.format(distance));
                    hot.setText(db.format(totalHot));
                    //时间转换
                    S=totalTime%60;
                    HMS=totalTime/60;
                    M=HMS%60;
                    H=HMS/60;

                    time.setText((H>=10?(H+""):("0"+H))+":"+(M>=10?(M+""):("0"+M))+":"+(S>=10?(S+""):("0"+S)));
                    time2.setText((H>=10?(H+""):("0"+H))+":"+(M>=10?(M+""):("0"+M))+":"+(S>=10?(S+""):("0"+S)));
                    //配速转换
                    S=tspeed%60;
                    M=tspeed/60;
                    if(tspeed!=0) {
                        speed.setText(M + "'" + S + "''");
                    }else {
                        speed.setText("--");
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_startsport);

        init(savedInstanceState);

    }

    private void init(@Nullable Bundle savedInstanceState) {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);
        title=findViewById(R.id.start_ly_title);
        back=findViewById(R.id.start_back);
        //一开始进去的布局
        visible=findViewById(R.id.start_visible);
        start=findViewById(R.id.start_btn);
        plan=findViewById(R.id.start_plan);
        //操作按钮显示信息的布局
        visible2=findViewById(R.id.start_visible2);
        slide=findViewById(R.id.start_slide);
        ly1=findViewById(R.id.start_ly1);
        km=findViewById(R.id.start_km);
        time=findViewById(R.id.start_time);
        speed=findViewById(R.id.start_speed);
        hot=findViewById(R.id.start_hot);
        ly2=findViewById(R.id.start_ly2);
        km2=findViewById(R.id.start_km2);
        time2=findViewById(R.id.start_time2);
        ly3=findViewById(R.id.start_ly3);
        play=findViewById(R.id.start_play);
        pause=findViewById(R.id.start_pause);
        stop=findViewById(R.id.start_stop);
        share=findViewById(R.id.start_share);

        start.setOnClickListener(this);
        slide.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        share.setOnClickListener(this);

        //获取地图控件引用
        map=(MapView)findViewById(R.id.start_loction);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        map.onCreate(savedInstanceState);

        if(aMap==null){
            aMap=map.getMap();
            initMapStyle();
            initLocation();
        }


        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        plan.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PlanRunning.class);
                startActivity(intent);
            }
        });
    }


    private void initLocation() {
        //定位相关初始化
        mapClient=new AMapLocationClient(this);
        mapOption=new AMapLocationClientOption();
        //设置定位监听
        mapClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battert_Saving为低功耗模式，Device_Sensors是仅设备模式
        mapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位时间间隔
        mapOption.setInterval(1000);
        //设置高精度模式下获取GPS速度
        mapOption.setSensorEnable(true);
        //设置单次定位
            /*mapOption.setOnceLocation(true);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mapOption.setOnceLocationLatest(true);*/
        //设置定位参数
        mapClient.setLocationOption(mapOption);
        //启动定位
        mapClient.startLocation();

    }

    private void initMapStyle() {
        //设置地图的类型
        //aMap.setMapType(AMap.MAP_TYPE_NAVI);
        //设置定位监听，要实现LoactionSource接口
        aMap.setLocationSource(this);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        style=new MyLocationStyle();
        //LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER:连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        //LOCATION_TYPE_FOLLOW_NO_CENTER:连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        //LOCATION_TYPE_MAP_ROTATE_NO_CENTER:连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效
        style.interval(1000);

        //自定义定位蓝点
        //图标
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked));
        //定位锚点
        //style.anchor((float) 0.0,(float) 0.3);
        //精度圈边框颜色
        style.strokeColor(Color.parseColor("#331B85FF"));
        //精度圈填充颜色
        style.radiusFillColor(Color.parseColor("#111B85FF"));
        //精度圈宽度
        //style.strokeWidth(5);

        //设置定位蓝点的Style
        aMap.setMyLocationStyle(style);
        //设置触发定位按钮是否显示，非必需设置。
        //aMap.getUiSettings().setMyLocationButtonEnabled(false);
        //设置缩放按钮是否显示，非必需设置。
        aMap.getUiSettings().setZoomControlsEnabled(false);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        //显示定位层，并且可以触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        //设置地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomBy(6));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        map.onDestroy();
        if(mapClient!=null) {
            mapClient.stopLocation();
            mapClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation!=null && mListener!=null){
            if(aMapLocation!=null && aMapLocation.getErrorCode()==0){
                //定位成功回调信息，设置相关消息

                //获取定位来源
                aMapLocation.getLocationType();
                //获取纬度
                aMapLocation.getLatitude();
                //获取经度
                aMapLocation.getLongitude();
                //获取精度信息
                aMapLocation.getAccuracy();
                Date date=new Date(aMapLocation.getTime());
                //定位的时间
                df.format(date);

                //判断是否在运动，如果是在运动则记录轨迹
                if(isStart) {
                    //计算距离
                    latLng1=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    if(latLng2==null){
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.sport_ending));
                        latLng2=latLng1;
                    }else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.sport_start_running));
                        //获取距离,AMapUtils.calculateLineDistance(latLng1,latLng2)返回 m
                        distance += AMapUtils.calculateLineDistance(latLng1,latLng2)/1000;
                        //获取配速(多少分钟跑一公里),aMapLocation.getSpeed()返回 m/s
                        //人一般步行的速度是1m/s，所以1m/s以下的相当于静止，不计算
                        if(aMapLocation.getSpeed()>1) {
                            tspeed = (int) (1000 / aMapLocation.getSpeed());
                        }else {
                            tspeed=0;
                        }
                        latLng2=latLng1;
                    }

                    //显示轨迹、标记
                    //设置数据源
                    polylineOptions.add(latLng1);
                    //polylineOptions.addAll(latLngs);
                    //设置线条宽度
                    polylineOptions.width(10);
                    //设置线条颜色
                    polylineOptions.color(Color.parseColor("#e67e22"));
                    //将线条加到地图上
                    polyline=aMap.addPolyline(polylineOptions);

                    //设置点
                    markerOptions.position(latLng1);
                    //设置图片

                    //将Maeker设置为贴地显示，可以双指下拉地图查看效果
                    markerOptions.setFlat(true);
                    //添加标记
                    marker=aMap.addMarker(markerOptions);
                    markers.add(marker);
                    //设置标记点显示
                    for (int i=0;i<markers.size();i++){
                        Marker mk=markers.get(i);
                        mk.setVisible(false);
                        if(i==0){
                            mk.setVisible(true);
                        }
                        if(i==markers.size()-1){
                            mk.setVisible(true);
                        }

                    }

                    totalTime+=1;
                    //跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
                    totalHot=distance*1.036*sp.getWeight();
                    op.insert("insert into SportLocation(UserID,RunID,Longitude,Latitude,Time,Speed) values(?,?,?,?,?,?)",
                            new String[]{sp.getID(), RunID, aMapLocation.getLongitude() + "", aMapLocation.getLatitude() + "", df.format(date), tspeed + ""});

                    handler.sendEmptyMessage(0x001);

                }
                //将地图移到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())));
                //刷新地图显示
                mListener.onLocationChanged(aMapLocation);

            }
            else{
                //显示错误信息
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener=onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener=null;
        if(mapClient!=null){
            mapClient.stopLocation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn:
                //点击开始跳转
                initData();
                if(isFinish) {
                    acquireWakeLock();
                    visible.setVisibility(View.GONE);
                    visible2.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    isStart=true;
                    isFirst=false;
                    //开始运动时地图轨迹、标记初始化
                    polylineOptions=new PolylineOptions();
                    markerOptions=new MarkerOptions();
                    markers=new ArrayList<>();
                }
                // 测试 SDK 是否正常工作的代码
                /*AVObject testObject = new AVObject("TestObject1111");
                testObject.put("words","Hello World!");
                testObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            Log.d("saved","success!");
                        }
                    }
                });*/
                break;
            case R.id.start_slide:
                if(isShow) {
                    isShow=!isShow;
                    ly1.setVisibility(View.GONE);
                    ly3.setVisibility(View.GONE);
                    ly2.setVisibility(View.VISIBLE);
                    slide.setImageResource(R.mipmap.slideup_pressed);
                }else {
                    isShow=!isShow;
                    ly1.setVisibility(View.VISIBLE);
                    ly3.setVisibility(View.VISIBLE);
                    ly2.setVisibility(View.GONE);
                    slide.setImageResource(R.mipmap.slidedown_pressed);
                }
                break;
            case R.id.start_stop:
                //退出
                isStart=false;
                if(System.currentTimeMillis()-mTime>2000)
                {
                    Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
                    mTime=System.currentTimeMillis();
                }
                else {
                    update();
                    super.onBackPressed();
                }
                break;
            case R.id.start_pause:
                //暂停
                isStart=false;
                tspeed=0;
                handler.sendEmptyMessage(0x001);
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                break;
            case R.id.start_play:
                //继续
                isStart=true;
                latLng2=null;
                pause.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                break;
            case R.id.start_share:
                //分享
                //判断是否达标，达标则将数据计入完成表中
                if(SportID.equals("2") && distance>=5 || SportID.equals("3") && distance>=10 || SportID.equals("4") && distance>=21.0975 || SportID.equals("5") && distance>=42.195)
                {
                    String title = "";
                    String content = "";
                    String time = TimeTools.getCurrentDate2();
                    if (SportID.equals("2")) {
                        title = "5公里跑步";
                    }
                    if (SportID.equals("3")) {
                        title = "10公里跑步";
                    }
                    if (SportID.equals("4")) {
                        title = "半程马拉松";
                    }
                    if (SportID.equals("5")) {
                        title = "马拉松";
                    }
                    content = "我在" + TimeTools.getCurrentDate() + "完成了" + title + "任务！";

                    op.insert("insert into Share(UserID,Title,Content,Date,Count) values(?,?,?,?,0)",
                            new String[]{sp.getID(), title, content, time});
                    //判断是否分享成功
                    List<Map<String, String>> data = new ArrayList<>();
                    data = op.select("select count(*) count from Share where UserID=? and Date=? ",
                            new String[]{sp.getID(), time});
                    if (data.get(0).get("count").equals("1")) {
                        //Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                        //sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.InteractSy$MyBroad")));
                    } else {
                        //Toast.makeText(context, "分享失败！请稍后重试", Toast.LENGTH_SHORT).show();
                    }

                    AVObject testObject1 = new AVObject("Share");
                    testObject1.put("UserID",sp.getID());
                    testObject1.put("Title",title);
                    testObject1.put("Content",content);
                    testObject1.put("Date",time);
                    testObject1.put("Count",0);
                    testObject1.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null){
                                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                                sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.InteractSy$MyBroad")));
                            }else{
                                Toast.makeText(context, "分享失败！请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(context, "运动未达标！请先完成运动再来分享呦~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void update() {
        //如果运动超过三分钟则更新运动表，否则删除该记录，并且删除轨迹记录。
        //if (totalTime > 180)
        {
            endTime = df.format(new Date());
            String rspeed = "0";
            List<Map<String, String>> data = new ArrayList<>();
            data = op.select("select sum(Speed) speed,count(*) count from SportLocation where UserID=? and RunID=? ",
                    new String[]{sp.getID(), RunID});
            if (data.size() != 0) {
                if (data.get(0).get("speed") != null && data.get(0).get("count") != null) {
                    double speed = new Double(data.get(0).get("speed"));
                    double count = new Double(data.get(0).get("count"));
                    rspeed = db2.format(speed / count);
                }
            }else {
                /*String cql = "select sum(Speed) speed,count(*) count from SportLocation where UserID=? and RunID=?";
                AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        if (e == null) {
                            // 操作成功
                            ArrayList<AVObject> list= (ArrayList<AVObject>) avCloudQueryResult.getResults();
                        } else {
                            e.printStackTrace();
                        }
                    }
                }, Arrays.asList(0, 1));*/
            }
            op.insert("update SportRunning set EndTime=?,Speed=?,Total=?,Time=?,Hot=? where StartTime=?",
                    new String[]{endTime, rspeed, db.format(distance) + "", totalTime + "", db.format(totalHot) + "", startTime});

            String cql = "update SportRunning set EndTime=?,Speed=?,Total=?,Time=?,Hot=? where StartTime=?";
            AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        // 操作成功
                    } else {
                        e.printStackTrace();
                    }
                }
            }, Arrays.asList(endTime, rspeed,db.format(distance)),totalTime,db.format(totalHot),startTime);
            //判断是否达标，达标则将数据计入完成表中
            if(SportID.equals("2") && distance>=5 || SportID.equals("3") && distance>=10 || SportID.equals("4") && distance>=21.0975 || SportID.equals("5") && distance>=42.195){
                op.insert("insert into SportFinish(UserID,SportID,Time) values(?,?,?)", new String[]{sp.getID(), SportID,TimeTools.getCurrentDate()});

                String cql1 = "insert into SportFinish(UserID,SportID,Time) values(?,?,?)";
                AVQuery.doCloudQueryInBackground(cql1, new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        if (e == null) {
                            // 操作成功
                        } else {
                            e.printStackTrace();
                        }
                    }
                }, Arrays.asList(sp.getID(), SportID,TimeTools.getCurrentDate()));
            }
            sendBroadcast(new Intent("com.develop.sport.MYBROAD3").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.HomeSy$MyBroad")));
        }
        //else
            {
            op.insert("delete from SportRunning where id=?",new String[]{RunID});
            op.insert("delete from SportLocation where RunID=?",new String[]{RunID});

            String cql2 = "delete from SportRunning where id=?";
            AVQuery.doCloudQueryInBackground(cql2, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        // 操作成功
                    } else {
                        e.printStackTrace();
                    }
                }
            }, Arrays.asList(RunID));
            String cql3 = "delete from SportLocation where RunID=?";
            AVQuery.doCloudQueryInBackground(cql3, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        // 操作成功
                    } else {
                        e.printStackTrace();
                    }
                }
            }, Arrays.asList(RunID));
        }
    }

    /**
    * 初始化跑步数据
    * */
    private void initData() {
        startTime=df.format(new Date());
        List<Map<String, String>> data = new ArrayList<>();
        data = op.select("select * from SportPlan where UserID=? and SportID<>1 and State=1", new String[]{sp.getID()});
        if (data.size() != 0) {
            SportID=data.get(0).get("SportID");
            op.select("insert into SportRunning(UserID,SportID,StartTime) values(?,?,?)", new String[]{sp.getID(),SportID,startTime});

            String cql3 = "insert into SportRunning(UserID,SportID,StartTime) values(?,?,?)";
            AVQuery.doCloudQueryInBackground(cql3, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        // 操作成功
                    } else {
                        e.printStackTrace();
                    }
                }
            }, Arrays.asList(sp.getID(),SportID,startTime));

            data = op.select("select * from SportRunning where UserID=? and SportID=? and StartTime=?", new String[]{sp.getID(),SportID,startTime});
            if (data.size() != 0) {
                isFinish=true;
                RunID=data.get(0).get("id");
            }else{
                isFinish=false;
                Toast.makeText(context,"初始化失败，请重试",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(context,"你还没有运动计划，请先创建计划",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(isFirst){
            super.onBackPressed();
        }else {
            Toast.makeText(context, "请先暂停，然后再双击退出按钮", Toast.LENGTH_SHORT).show();
        }
    }



    // 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                // Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            // Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }
}
