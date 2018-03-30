package com.develop.sporthealth;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.develop.bean.Running;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/23.
 */

public class HomeRunningShow extends AppCompatActivity{

    private String RunID="";
    private Context context;
    private SQLOperator op;
    private SPTools sp;

    private MyLayout back;
    private MapView map;

    //声明地图控制器
    private AMap aMap;
    //定义地图画线条
    private PolylineOptions polylineOptions;
    //定义地图点
    private List<LatLng> latLngs=new ArrayList<>();
    //定义地图图层
    private Marker marker;
    private List<Marker> markers;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_run_show);

        init(savedInstanceState);

        getData();

        //showMap();

    }

    private void init(@Nullable Bundle savedInstanceState) {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);

        back=findViewById(R.id.homerun_show_back);
        map=findViewById(R.id.homerun_show_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        map.onCreate(savedInstanceState);

        if(aMap==null){
            aMap=map.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            //设置地图的缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomBy(6));
        }

        //开始运动时地图轨迹、标记初始化
        polylineOptions=new PolylineOptions();
        markerOptions=new MarkerOptions();
        markers=new ArrayList<>();

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        RunID=getIntent().getStringExtra("RunID");
        //显示统计数据
        AVQuery<AVObject> query = new AVQuery<>("SportLocation");
        query.whereEqualTo("RunID",RunID);
        query.orderByAscending("Time");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                    String date1 = "";
                    String date2 = "";
                    int i = 0;
                    double total = 0;
                    for (; i < list.size(); i++) {
                        latLngs.add(new LatLng(new Double(list.get(i).get("Latitude").toString()), new Double(list.get(i).get("Longitude").toString())));
                    }
                    showMap();
                }
            }
        });

        /*List<Map<String, String>> data = new ArrayList<>();
        data = op.select("select * from SportLocation where RunID=? order by Time asc", new String[]{RunID});
        if (data.size() != 0) {
            for (int i=0;i<data.size();i++) {
                latLngs.add(new LatLng(new Double(data.get(i).get("Latitude")), new Double(data.get(i).get("Longitude"))));
            }
        }*/
    }

    private void showMap() {
        for (int i=0;i<latLngs.size();i++) {
            //显示轨迹、标记
            //设置数据源
            polylineOptions.add(latLngs.get(i));
            //polylineOptions.addAll(latLngs);
            //设置线条宽度
            polylineOptions.width(10);
            //设置线条颜色
            polylineOptions.color(Color.parseColor("#e67e22"));
            //将线条加到地图上
            aMap.addPolyline(polylineOptions);

            //设置点
            markerOptions.position(latLngs.get(i));
            //设置图片

            //将Maeker设置为贴地显示，可以双指下拉地图查看效果
            markerOptions.setFlat(true);
            if (i == 0){
                //设置显示的图片
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.sport_start_running));
                //添加标记
                marker = aMap.addMarker(markerOptions);
                markers.add(marker);
            }
            if(i==latLngs.size()-1){
                //设置显示的图片
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.sport_ending));
                //添加标记
                marker = aMap.addMarker(markerOptions);
                markers.add(marker);
            }

            //将地图移到定位点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngs.get(i)));

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }
}
