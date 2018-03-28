package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.develop.tools.AppManager;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/20.
 */

public class PlanRunning extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private MyLayout back;
    private Button next;
    private RadioGroup running;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;

    private static int target=0;
    private SPTools sp;
    private SQLOperator op;
    private Context context;

    private String date;

    private boolean isData=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.plan_running);

        //页面管理
        AppManager.getAppManager().addActivity(PlanRunning.this);

        init();

        getData();
    }

    private void init() {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);

        back=findViewById(R.id.running_back);
        next =findViewById(R.id.running_next);

        running =findViewById(R.id.running_rg);
        rb1 =findViewById(R.id.running_1);
        rb2 =findViewById(R.id.running_2);
        rb3 =findViewById(R.id.running_3);
        rb4 =findViewById(R.id.running_4);

        next.setOnClickListener(this);
        running.setOnCheckedChangeListener(this);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        data = op.select("select * from SportPlan where UserID=? and SportID<>1 and State=1", new String[]{sp.getID()});
        if (data.size() != 0) {
            isData=true;
            map = data.get(0);
            if(map.get("SportID").equals("2")){
                rb1.setChecked(true);
            }else if(map.get("SportID").equals("3")){
                rb2.setChecked(true);
            }else if(map.get("SportID").equals("4")){
                rb3.setChecked(true);
            }else if(map.get("SportID").equals("5")){
                rb4.setChecked(true);
            }

        }else {
            AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
            query1.whereEqualTo("UserID",sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
            query2.whereNotEqualTo("SportID",1);
            AVQuery<AVObject> query3 = new AVQuery<>("SportPlan");
            query2.whereEqualTo("State",1);
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2,query3));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(list.size()>0) {
                        isData = true;
                        if (list.get(0).get("SportID").equals("2")) {
                            rb1.setChecked(true);
                        } else if (list.get(0).get("SportID").equals("3")) {
                            rb2.setChecked(true);
                        } else if (list.get(0).get("SportID").equals("4")) {
                            rb3.setChecked(true);
                        } else if (list.get(0).get("Target").equals("5")) {
                            rb4.setChecked(true);
                        }

                    }

                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.running_next:
                if(sp.getIsLogin()) {
                    if (target == 0) {
                        Toast.makeText(context, "你还没有设置目标，请先选择", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, PlanRunningNumber.class);
                        intent.putExtra("tag", target);
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        setSelected();
        switch (checkedId){
            case R.id.running_1:
                rb1.setChecked(true);
                target=2;
                break;
            case R.id.running_2:
                rb2.setChecked(true);
                target=3;
                break;
            case R.id.running_3:
                rb3.setChecked(true);
                target=4;
                break;
            case R.id.running_4:
                rb4.setChecked(true);
                target=5;
                break;

        }
    }

    public void setSelected(){
        rb1.setSelected(false);
        rb2.setSelected(false);
        rb3.setSelected(false);
        rb4.setSelected(false);
    }

}
