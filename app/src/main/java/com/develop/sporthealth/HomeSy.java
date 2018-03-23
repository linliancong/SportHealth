package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.develop.bean.StepEntity;
import com.develop.tools.SPTools;
import com.develop.tools.StepCountCheckUtil;
import com.develop.tools.StepService;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Administrator on 2018/3/16.
 */

public class HomeSy extends Fragment implements View.OnClickListener,android.os.Handler.Callback,SensorEventListener{

    private Context context;
    private View view;

    private ImageView progress;
    private TextView target;
    private TextView step;
    private TextView heat;
    private TextView sport1;
    private TextView sport2;
    private TextView count;
    private Button start;

    private RelativeLayout step_rl;
    private RelativeLayout chart_rl;

    //柱形图相关设置
    private ColumnChartView column;
    private List<String> data=new ArrayList<>();
    private List<String> score=new ArrayList<>();
    private List<SubcolumnValue> mPointValue=new ArrayList<>();
    private List<AxisValue> mAxisXValue=new ArrayList<>();
    private List<AxisValue> mAxisYValue=new ArrayList<>();

    //步数传感器相关
    private int mDetector;
    private SensorManager  mSensorManager;
    private Sensor mStepCount;
    private Sensor mStepDetector;


    private SQLOperator op;
    private SPTools sp;
    private String curSelDate;
    private DecimalFormat df = new DecimalFormat("#.##");
    private DecimalFormat df2 = new DecimalFormat("#");
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf2=new SimpleDateFormat("MM-dd");


    public HomeSy(){}
    @SuppressLint("ValidFragment")
    public HomeSy(Context context){
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.home, container, false);

            init();

            /**
             * 这里判断当前设备是否支持计步
             */
            if (StepCountCheckUtil.isSupportStepCountSensor(context)) {
                getStep();
                setDatas();
                setupService();
            }

        }

        try {
            getChartDate();
            getAxisXLables();
            initLineChart();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return view;
    }

    private void init() {

        op=new SQLOperator(context);
        sp=new SPTools(context);
        progress=view.findViewById(R.id.home_progress);
        target=view.findViewById(R.id.home_target);
        step=view.findViewById(R.id.home_reality);
        heat=view.findViewById(R.id.home_heat);
        sport1=view.findViewById(R.id.home_sport1);
        sport2=view.findViewById(R.id.home_sport2);
        count=view.findViewById(R.id.home_count);
        column=view.findViewById(R.id.home_column);
        start=view.findViewById(R.id.home_start);
        step_rl=view.findViewById(R.id.home_rl1);
        chart_rl=view.findViewById(R.id.home_rl2);

        start.setOnClickListener(this);
        step_rl.setOnClickListener(this);
        chart_rl.setOnClickListener(this);

        curSelDate = TimeTools.getCurrentDate();
    }



    /**
     *设置X轴的显示
     * */
    public void getAxisXLables(){
        mAxisXValue=new ArrayList<>();
        for (int i=0;i<data.size();i++){
            mAxisXValue.add(new AxisValue(i).setValue(i).setLabel(data.get(i)));
        }
    }

    /**
     * 对图表进行设置
     * */
    public void initLineChart(){
        List<Column> columns=new ArrayList<>();
        float value;
        Float it;
        for (int i=0;i<score.size();i++){
            //为每一柱图添加颜色和数值
            mPointValue=new ArrayList<>();
            it=new Float(score.get(i));
            value=it.floatValue();
            mPointValue.add(new SubcolumnValue(value, ChartUtils.pickColor()));
            Column column1=new Column(mPointValue);
            //设置显示小数点
            ColumnChartValueFormatter formatter=new SimpleColumnChartValueFormatter(2);
            column1.setFormatter(formatter);
            //曲线的数据坐标是否加上备注
            column1.setHasLabels(true);
            //是否是点击圆柱才显示数据标注
            column1.setHasLabelsOnlyForSelected(false);

            columns.add(column1);
        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        ColumnChartData data1=new ColumnChartData();
        data1.setColumns(columns);


        //坐标轴
        //X轴
        Axis axisX=new Axis();
        //X轴字体市斜的显示还是直的，设置为true为斜的
        axisX.setHasTiltedLabels(true);
        //字体大小
        axisX.setTextSize(10);
        //最多几个坐标轴，mAxisValue.lenght
        axisX.setMaxLabelChars(7);
        //填充X的坐标名
        axisX.setValues(mAxisXValue);
        //X轴的位置在底部
        data1.setAxisXBottom(axisX);

        //Y轴 根据数据大小自动设置上限
        Axis axisY=new Axis();
        //字体大小
        axisY.setTextSize(10);

        //设置固定Y轴数据，从0-30
        for (int i=0;i<=30;i+=2){
            mAxisYValue.add(new AxisValue(i).setValue(i));
        }

        axisY.setValues(mAxisYValue);
        //Y轴设置在左边
        data1.setAxisYLeft(axisY);

        //滑动及平移
        column.setZoomType(ZoomType.HORIZONTAL);
        column.setMaxZoom((float)2);//最大方法比例
        column.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        //设置是否支持缩放
        column.setZoomEnabled(true);
        //设置图表是否可以与用户互动
        column.setInteractive(true);
        //设置图表显示的数据
        column.setColumnChartData(data1);
        column.setVisibility(View.VISIBLE);


        Viewport v=new Viewport(column.getMaximumViewport());
        //这里设置Y轴的值的范围
        v.bottom=0;
        v.top=30;
        column.setMaximumViewport(v);
        //这里设置一开始加载的显示个数，配合axisX.setMaxLabelChars(7);使用，这里的7必须大于等于设置的范围，才会生效
        v.left=0;
        v.right=6;
        column.setCurrentViewport(v);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_start:
                Intent intent=new Intent(context,HomeStartSport.class);
                startActivity(intent);
                break;
            case R.id.home_rl1:
                //步行分析
                /*Intent intent2=new Intent(context,HomeStep.class);
                startActivity(intent2);*/
                break;
            case R.id.home_rl2:
                //跑步分析
                Intent intent3=new Intent(context,HomeRunning.class);
                startActivity(intent3);
                break;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mSensorManager.unregisterListener(this, mStepDetector);

        mSensorManager.unregisterListener(this, mStepCount);

    }

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(context, StepService.class);
        intent.setAction("com.develop.tools.STEPSERVICE");
        context.startService(intent);
    }

    /**
     * 设置记录数据
     */
    private void setDatas() {
        StepEntity stepEntity = op.getCurDataByDate(curSelDate);

        if (stepEntity != null) {
            int steps = Integer.parseInt(stepEntity.getSteps());

            //获取全局的步数
            step.setText(String.valueOf(steps));
            //计算总公里数
            sport1.setText(countTotalKM(steps));
            //计算卡路里
            heat.setText(countTotalHeat(steps));
            //显示进度
            showImagth(steps);
        } else {
            //获取全局的步数
            step.setText("0");
            //计算总公里数
            sport1.setText("0");
            //计算卡路里
            heat.setText("0");
            //显示进度
            showImagth(0);
        }

    }

    /**
     * 简单的计算卡路里
     * 跑步卡路里（kcal）＝体重（kg）×距离（公里）×1.036
     * 走路卡路里=步数*0.042
     * @param steps 用户当前步数
     * @return
     */
    private String countTotalHeat(int steps) {
        double totalMeters = steps * 0.042;
        //保留整数
        return df2.format(totalMeters);
    }

    /**
     * 简易计算公里数，假设一步大约有0.7米
     * @param steps 用户当前步数
     * @return
     */
    private String countTotalKM(int steps) {
        double totalMeters = steps * 0.7;
        //保留两位有效数字
        return df.format(totalMeters / 1000);
    }

    /**
     * 设置进度显示
     * @param steps 用户当前步数
     * @return
     */
    private void showImagth(int steps) {
        double targ=new Integer(target.getText().toString()).intValue();
        double prog = (steps / targ)*100;
        if(prog==0){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_0));
        }else if(prog<6){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_1));
        } else if(prog<10){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_2));
        }else if(prog<13){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_3));
        }else if(prog<16){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_4));
        }else if(prog<20){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_5));
        }else if(prog<23){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_6));
        }else if(prog<26){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_7));
        }else if(prog<30){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_8));
        }else if(prog<33){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_9));
        }else if(prog<36){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_10));
        }else if(prog<40){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_11));
        }else if(prog<43){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_12));
        }else if(prog<46){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_13));
        }else if(prog<50){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_14));
        }else if(prog<53){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_15));
        }else if(prog<56){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_16));
        }else if(prog<60){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_17));
        }else if(prog<63){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_18));
        }else if(prog<66){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_19));
        }else if(prog<70){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_20));
        }else if(prog<73){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_21));
        }else if(prog<76){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_22));
        }else if(prog<80){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_23));
        }else if(prog<83){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_24));
        }else if(prog<86){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_25));
        }else if(prog<90){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_26));
        }else if(prog<93){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_27));
        }else if(prog<100){
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_28));
        }else if(prog>=100){
            //当步数完成的时候，判断完成表中是不是有数据
            List<Map<String, String>> data = new ArrayList<>();
            data = op.select("select count(*) num from SportFinish where UserID=? and SportID=1 and Time=?", new String[]{sp.getID(), TimeTools.getCurrentDate()});
            if(data.size()!=0) {
                if(data.get(0).get("num").equals("0")) {
                    op.insert("insert into SportFinish(UserID,SportID,Time) values(?,1,?)", new String[]{sp.getID(), TimeTools.getCurrentDate()});
                }
            }
            progress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.progress_29));
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            //这里用来获取到Service发来的数据
            case StepService.MSG_FROM_SERVER:

                //如果是今天则更新数据
                if (curSelDate.equals(TimeTools.getCurrentDate())) {
                    //记录运动步数
                    int steps = msg.getData().getInt("steps");
                    //设置的步数
                    step.setText(String.valueOf(steps));
                    //计算总公里数
                    sport1.setText(countTotalKM(steps));
                }
                break;
        }
        return false;
    }

    /**
     * 初始化步数传感器
     */
    private void getStep() {
        //1、使用传感器之前首先获取SensorManager通过系统服务获取：
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        //2、获取我们需要的传感器类型：
        //单次有效计步
        mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //系统计步累加值
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //3、注册监听者（监听传感器事件）
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //如果步数变化了就刷新
        setDatas();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 设置图标的数据
    * */
    private void getChartDate() throws ParseException {
        //显示目标数据
        List<Map<String, String>> datas = new ArrayList<>();
        datas = op.select("select *  from SportPlan where UserID=? and SportID=1 and State=1", new String[]{sp.getID()});
        if(datas.size()!=0) {
            target.setText(datas.get(0).get("Target"));
        }else {
            target.setText("10000");
        }
        //显示统计数据
        //这里先删除无效数据，避免出现异常
        op.insert("delete from SportRunning where UserID=? and Total is null",new String[]{sp.getID()});
        List<Map<String, String>> maps = new ArrayList<>();
        maps = op.select("select * from SportRunning where UserID=? order by StartTime", new String[]{sp.getID()});
        if (maps.size() != 0) {
            String date1="";
            String date2="";
            int i=0;
            double total=0;
            for (;i<maps.size();i++) {
                //把日期先转为标准格式，再进行我们需要的变化
                date1=sdf2.format(sdf.parse(maps.get(i).get("StartTime")));
                if(!date1.equals(date2)){
                    data.add(date1);
                    score.add(maps.get(i).get("Total"));
                }else {
                    double n=new Double(score.get(score.size()-1));
                    double m=new Double(maps.get(i).get("Total"));
                    score.remove(score.size()-1);
                    score.add(df.format(n+m));
                }
                total+=new Double(maps.get(i).get("Total"));
                date2=date1;

            }
            sport2.setText(df.format(total));
            count.setText(i+"");
        }

        //以下数据为测试数据
        //X轴的标注
       /* data.add("3-1");
        data.add("3-15");
        data.add("4-16");
        data.add("5-21");
        data.add("6-17");
        data.add("7-28");
        data.add("8-31");
        data.add("9-17");
        data.add("10-28");
        data.add("11-31");*/

        //图表的数据点
       /* score.add("3.5");
        score.add("1.7");
        score.add("6.5");
        score.add("14.3");
        score.add("2.1");
        score.add("5.3");
        score.add("17.1");
        score.add("2.1");
        score.add("5.3");
        score.add("17.1");*/
    }
}
