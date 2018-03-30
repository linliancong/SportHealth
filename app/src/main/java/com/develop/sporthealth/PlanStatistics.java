package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

/**
 * Created by Administrator on 2018/3/30.
 */

public class PlanStatistics extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {

    private TextView count;
    private TextView sport;
    private Context context;
    private SPTools sp;
    private RelativeLayout statis_click;
    private MyLayout back;

    //柱形图相关设置
    private ColumnChartView column;
    private List<String> data=new ArrayList<>();
    private List<String> score=new ArrayList<>();
    private List<SubcolumnValue> mPointValue=new ArrayList<>();
    private List<AxisValue> mAxisXValue=new ArrayList<>();
    private List<AxisValue> mAxisYValue=new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("#.##");
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf2=new SimpleDateFormat("MM-dd");

    //类型查询
    private RadioGroup rg;
    private RadioButton rb_week;
    private RadioButton rb_month;
    private RadioButton rb_year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_statistics);

        back=findViewById(R.id.statis_back);
        context=getApplicationContext();
        sp=new SPTools(context);
        count=findViewById(R.id.statis_count);
        column=findViewById(R.id.statis_column);
        sport=findViewById(R.id.statis_sport);
        statis_click=findViewById(R.id.statis_rl2);

        //类型查询
        rg=findViewById(R.id.statis_rg_type);
        rb_week =findViewById(R.id.statis_rb_qb);
        rb_month =findViewById(R.id.statis_rb_wcl);
        rb_year =findViewById(R.id.statis_rb_ycl);
        rg.setOnCheckedChangeListener(this);

        statis_click.setOnClickListener(this);
        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getChartDate(0);
        /*getAxisXLables();
        initLineChart();*/

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

    /**
     * 设置图标的数据
     * */
    private void getChartDate(int type){
        data=new ArrayList<>();
        score=new ArrayList<>();
        //显示统计数据
        AVQuery<AVObject> query1 = new AVQuery<>("SportRunning");
        query1.whereEqualTo("UserID",sp.getID());
        AVQuery<AVObject> query2 = new AVQuery<>("SportRunning");
        if(type==1) {
            query2.whereGreaterThanOrEqualTo("StartTime", TimeTools.getCurMonth());
        }else if(type==2){
            query2.whereGreaterThanOrEqualTo("StartTime", TimeTools.getCurYear());
        }else {
            query2.whereGreaterThanOrEqualTo("StartTime", TimeTools.getCurWeek());
        }
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.orderByAscending("StartTime");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0) {
                    String date1="";
                    String date2="";
                    int i=0;
                    double total=0;
                    for (;i<list.size();i++) {
                        //把日期先转为标准格式，再进行我们需要的变化
                        try {
                            date1=sdf2.format(sdf.parse(list.get(i).get("StartTime").toString()));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        if(!date1.equals(date2)){
                            data.add(date1);
                            score.add(list.get(i).get("Total").toString());
                        }else {
                            double n=new Double(score.get(score.size()-1));
                            double m=new Double(list.get(i).get("Total").toString());
                            score.remove(score.size()-1);
                            score.add(df.format(n+m));
                        }
                        total+=new Double(list.get(i).get("Total").toString());
                        date2=date1;

                    }
                    sport.setText(df.format(total));
                    count.setText(i+"");
                    getAxisXLables();
                    initLineChart();
                }else{
                    column.setColumnChartData(null);
                    sport.setText("0");
                    count.setText("0");
                    Toast.makeText(context,"无数据",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.statis_rl2:
                //跑步分析
                Intent intent3=new Intent(context,HomeRunning.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.statis_rb_qb:
                setChecked();
                rb_week.setChecked(true);
                getChartDate(0);
                break;
            case R.id.statis_rb_wcl:
                setChecked();
                rb_month.setChecked(true);
                getChartDate(1);
                break;
            case R.id.statis_rb_ycl:
                setChecked();
                rb_year.setChecked(true);
                getChartDate(2);
                break;
        }
    }

    public void setChecked(){
        rb_week.setChecked(false);
        rb_month.setChecked(false);
        rb_year.setChecked(false);
    }
}
