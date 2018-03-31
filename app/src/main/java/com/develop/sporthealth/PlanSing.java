package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;
import com.develop.tools.singtools.ResolutionUtil;
import com.develop.tools.singtools.SignAdapter;
import com.develop.tools.singtools.SignDialogFragment;
import com.develop.tools.singtools.SignEntity;
import com.develop.tools.singtools.SignView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class PlanSing extends AppCompatActivity {
    /*private TextView tvSignDay;
    private TextView tvScore;*/
    private TextView tvYear;
    private TextView tvMonth;
    private SignView signView;
    private AppCompatButton btnSign;
    private MyLayout back;
    private List<SignEntity> data;

    private Context context;
    private SPTools sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_sign);

        initView();
        getData();
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        /*tvSignDay = findViewById(R.id.plan_sign_day);
        tvScore = findViewById(R.id.plan_sign_score);*/
        tvYear = findViewById(R.id.plan_sign_year);
        tvMonth = findViewById(R.id.plan_sign_month);
        signView = findViewById(R.id.plan_sign_view);
        btnSign = findViewById(R.id.plan_sign_btn);
        back=findViewById(R.id.plan_sign_back);
        context=getApplicationContext();
        sp=new SPTools(context);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (signView != null) {
            signView.setOnTodayClickListener(onTodayClickListener);
        }
        if (btnSign != null) {
            //noinspection deprecation
            btnSign.setSupportBackgroundTintList(getResources().getColorStateList(R.color.carrot));
            btnSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signToday();
                }
            });
        }

        //---------------------------------分辨率适配----------------------------------
        ResolutionUtil resolutionUtil = ResolutionUtil.getInstance();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = resolutionUtil.formatVertical(40);
        /*tvSignDay.setLayoutParams(layoutParams);
        tvSignDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, resolutionUtil.formatVertical(42));*/

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = resolutionUtil.formatVertical(40);
       /* tvScore.setLayoutParams(layoutParams);
        tvScore.setTextSize(TypedValue.COMPLEX_UNIT_PX, resolutionUtil.formatVertical(95));*/

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resolutionUtil.formatVertical(130));
        layoutParams.topMargin = resolutionUtil.formatVertical(54);
        View llDate = findViewById(R.id.plan_sign_ly_date);
        if (llDate != null) {
            llDate.setLayoutParams(layoutParams);
        }

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = resolutionUtil.formatHorizontal(43);
        tvYear.setLayoutParams(layoutParams);
        tvYear.setTextSize(TypedValue.COMPLEX_UNIT_PX, resolutionUtil.formatVertical(43));

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = resolutionUtil.formatHorizontal(44);
        tvMonth.setLayoutParams(layoutParams);
        tvMonth.setTextSize(TypedValue.COMPLEX_UNIT_PX, resolutionUtil.formatVertical(43));

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resolutionUtil.formatVertical(818));
        signView.setLayoutParams(layoutParams);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resolutionUtil.formatVertical(142));
        layoutParams.topMargin = resolutionUtil.formatVertical(111);
        layoutParams.leftMargin = layoutParams.rightMargin = resolutionUtil.formatHorizontal(42);
        if (btnSign != null) {
            btnSign.setLayoutParams(layoutParams);
            btnSign.setTextSize(TypedValue.COMPLEX_UNIT_PX, resolutionUtil.formatVertical(54));
        }
    }

    //获取签到的时间
    private void getData() {
        Calendar calendar = Calendar.getInstance();
        final int month = calendar.get(Calendar.MONTH);

       /* tvSignDay.setText(Html.fromHtml(String.format(getString(R.string.you_have_sign), "#999999", "#1B89CD", 3)));
        tvScore.setText(String.valueOf(3015));*/
        tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvMonth.setText(getResources().getStringArray(R.array.month_array)[month]);

        final Calendar calendarToday = Calendar.getInstance();
        final int dayOfMonthToday = calendarToday.get(Calendar.DAY_OF_MONTH);

        final int mon= calendar.getActualMaximum(Calendar.DATE);

        data = new ArrayList<>();
        //查询签到数据
        AVQuery<AVObject> query1 = new AVQuery<>("Sign");
        query1.whereEqualTo("UserID",sp.getID());
        AVQuery<AVObject> query2 = new AVQuery<>("Sign");
        query2.whereEqualTo("Month", TimeTools.getCurrentDate3());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.orderByAscending("Data");
        query.findInBackground(new FindCallback<AVObject>() {
               @SuppressLint("ResourceAsColor")
               @Override
               public void done(List<AVObject> list, AVException e) {
                   if (list.size() > 0) {
                       //这里初始化所有的日期
                       for (int i = 0; i < mon; i++) {
                           SignEntity signEntity = new SignEntity();
                           if (dayOfMonthToday == i + 1)
                               signEntity.setDayType(2);
                           else {
                               signEntity.setDayType(1);
                           }
                           data.add(signEntity);
                       }
                       //这里设置签到的日期
                       for (int j=0;j<list.size();j++){
                           if(list.get(j).get("Day")!=null){
                               int day=new Integer(list.get(j).get("Day").toString());
                               if(list.get(j).get("State")!=null && list.get(j).get("State").equals("0")){
                                   data.get(day-1).setDayType(0);
                                   if(list.get(j).get("Day").equals(dayOfMonthToday+"")){
                                       btnSign.setEnabled(false);
                                       btnSign.setBackgroundColor(R.color.tm_white);
                                       btnSign.setText(R.string.have_signed);
                                   }
                               }
                           }
                       }
                   }else {
                       for (int i = 0; i < mon; i++) {
                           SignEntity signEntity = new SignEntity();
                           if (dayOfMonthToday == i + 1)
                               signEntity.setDayType(2);
                           else {
                               signEntity.setDayType(1);
                           }
                           data.add(signEntity);
                       }
                   }
                   SignAdapter signAdapter = new SignAdapter(data);
                   signView.setAdapter(signAdapter);
               }
           });

    }

    private void onSign() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignDialogFragment signDialogFragment = SignDialogFragment.newInstance(15);
        signDialogFragment.setOnConfirmListener(onConfirmListener);
        signDialogFragment.show(fragmentManager, SignDialogFragment.TAG);
    }

    @SuppressLint("ResourceAsColor")
    private void signToday() {

        AVObject testObject1 = new AVObject("Sign");
        testObject1.put("UserID",sp.getID());
        testObject1.put("Date",TimeTools.getCurrentDate());
        testObject1.put("Month", TimeTools.getCurrentDate3());
        testObject1.put("State","0");
        testObject1.put("Day",TimeTools.getCurrentDay()+"");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    data.get(signView.getDayOfMonthToday() - 1).setDayType(SignView.DayType.SIGNED.getValue());
                    signView.notifyDataSetChanged();
                    btnSign.setEnabled(false);
                    btnSign.setBackgroundColor(R.color.tm_white);
                    btnSign.setText(R.string.have_signed);
                    /*int score = Integer.valueOf((String) tvScore.getText());
                    tvScore.setText(String.valueOf(score + 15));*/
                    Toast.makeText(context,"签到成功！",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context,"签到失败！请稍后重试",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private SignView.OnTodayClickListener onTodayClickListener = new SignView.OnTodayClickListener() {
        @Override
        public void onTodayClick() {
            //onSign();
        }
    };

    private SignDialogFragment.OnConfirmListener onConfirmListener = new SignDialogFragment.OnConfirmListener() {
        @Override
        public void onConfirm() {
            signToday();
        }
    };

}
