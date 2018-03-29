package com.develop.sporthealth;

import android.content.Context;
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
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.AppManager;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/20.
 */

public class PlanWalk extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private MyLayout back;
    private Button save;
    private RadioGroup walk;
    private RadioButton rb2;
    private RadioButton rb4;
    private RadioButton rb6;
    private RadioButton rb8;
    private RadioButton rb10;
    private RadioButton rb12;
    private RadioButton rb14;
    private RadioButton rb16;
    private RadioButton rb18;
    private RadioButton rb20;

    private static int target=0;
    private SPTools sp;
    private SQLOperator op;
    private Context context;

    private String date;
    private boolean isData=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_walk);

        init();

        getData();
    }


    private void init() {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);

        back=findViewById(R.id.walk_back);
        save=findViewById(R.id.walk_save);

        walk=findViewById(R.id.walk_rg);
        rb2=findViewById(R.id.walk_2);
        rb4=findViewById(R.id.walk_4);
        rb6=findViewById(R.id.walk_6);
        rb8=findViewById(R.id.walk_8);
        rb10=findViewById(R.id.walk_10);
        rb12=findViewById(R.id.walk_12);
        rb14=findViewById(R.id.walk_14);
        rb16=findViewById(R.id.walk_16);
        rb18=findViewById(R.id.walk_18);
        rb20=findViewById(R.id.walk_20);

        save.setOnClickListener(this);

        walk.setOnCheckedChangeListener(this);

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
        data = op.select("select * from SportPlan where UserID=? and SportID=1 and State=1", new String[]{sp.getID()});
        if (data.size() != 0) {
            isData=true;
            map = data.get(0);
            if(map.get("Target").equals("2000")){
                rb2.setChecked(true);
            }else if(map.get("Target").equals("4000")){
                rb4.setChecked(true);
            }else if(map.get("Target").equals("6000")){
                rb6.setChecked(true);
            }else if(map.get("Target").equals("8000")){
                rb8.setChecked(true);
            }else if(map.get("Target").equals("10000")){
                rb10.setChecked(true);
            }else if(map.get("Target").equals("12000")){
                rb12.setChecked(true);
            }else if(map.get("Target").equals("14000")){
                rb14.setChecked(true);
            }else if(map.get("Target").equals("16000")){
                rb16.setChecked(true);
            }else if(map.get("Target").equals("18000")){
                rb18.setChecked(true);
            }else if(map.get("Target").equals("20000")){
                rb20.setChecked(true);
            }

        }else {
            AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
            query1.whereEqualTo("UserID",sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
            query2.whereEqualTo("SportID","1");
            AVQuery<AVObject> query3 = new AVQuery<>("SportPlan");
            query2.whereEqualTo("State","1");
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2,query3));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(list.size()>0) {
                        isData = true;
                        if(list.get(0).get("Target").equals("2000")){
                            rb2.setChecked(true);
                        }else if(list.get(0).get("Target").equals("4000")){
                            rb4.setChecked(true);
                        }else if(list.get(0).get("Target").equals("6000")){
                            rb6.setChecked(true);
                        }else if(list.get(0).get("Target").equals("8000")){
                            rb8.setChecked(true);
                        }else if(list.get(0).get("Target").equals("10000")){
                            rb10.setChecked(true);
                        }else if(list.get(0).get("Target").equals("12000")){
                            rb12.setChecked(true);
                        }else if(list.get(0).get("Target").equals("14000")){
                            rb14.setChecked(true);
                        }else if(list.get(0).get("Target").equals("16000")){
                            rb16.setChecked(true);
                        }else if(list.get(0).get("Target").equals("18000")){
                            rb18.setChecked(true);
                        }else if(list.get(0).get("Target").equals("20000")){
                            rb20.setChecked(true);
                        }
                    }

                }
            });

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.walk_save:
                if(sp.getIsLogin()) {
                    if (target == 0) {
                        Toast.makeText(context, "你还没有设置目标，请先选择", Toast.LENGTH_SHORT).show();
                    } else {
                        insert();
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
            case R.id.walk_2:
                rb2.setChecked(true);
                target=2000;
                break;
            case R.id.walk_4:
                rb4.setChecked(true);
                target=4000;
                break;
            case R.id.walk_6:
                rb6.setChecked(true);
                target=6000;
                break;
            case R.id.walk_8:
                rb8.setChecked(true);
                target=8000;
                break;
            case R.id.walk_10:
                rb10.setChecked(true);
                target=10000;
                break;
            case R.id.walk_12:
                rb12.setChecked(true);
                target=12000;
                break;
            case R.id.walk_14:
                rb14.setChecked(true);
                target=14000;
                break;
            case R.id.walk_16:
                rb16.setChecked(true);
                target=16000;
                break;
            case R.id.walk_18:
                rb18.setChecked(true);
                target=18000;
                break;
            case R.id.walk_20:
                rb20.setChecked(true);
                target=20000;
                break;
        }
    }

    public void setSelected(){
        rb2.setSelected(false);
        rb4.setSelected(false);
        rb6.setSelected(false);
        rb8.setSelected(false);
        rb10.setSelected(false);
        rb12.setSelected(false);
        rb14.setSelected(false);
        rb16.setSelected(false);
        rb18.setSelected(false);
        rb20.setSelected(false);
    }

    public void insert(){
        date= TimeTools.getCurrentDate();
        if(isData){
            op.insert("update SportPlan set State=2,FinalDate=? where UserID=? and SportID=1 and State=1",new String[]{date,sp.getID()});
            AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
            query1.whereEqualTo("UserID",sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
            query2.whereEqualTo("SportID","1");
            AVQuery<AVObject> query3 = new AVQuery<>("SportPlan");
            query3.whereEqualTo("State","1");
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2,query3));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(list.size()>0) {
                        // 第一参数是 className,第二个参数是 objectId
                        for (int i=0;i<list.size();i++) {
                            AVObject testObject1 = AVObject.createWithoutData("SportPlan", list.get(i).getObjectId());
                            testObject1.put("State", "2");
                            testObject1.put("FinalDate", TimeTools.getCurrentDate());
                            // 保存到云端
                            testObject1.saveInBackground();
                        }
                    }

                }
            });
        }
        op.insert("insert into SportPlan(UserID,SportID,State,StartDate,Target) values(?,?,?,?,?)", new String[]{sp.getID(), "1", "1", date, target + ""});
        AVObject testObject1 = new AVObject("SportPlan");
        testObject1.put("UserID",sp.getID());
        testObject1.put("SportID", "1");
        testObject1.put("State","1");
        testObject1.put("Target", target+"");
        testObject1.put("StartDate",date);
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    Toast.makeText(context, "目标保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(context, "目标保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
