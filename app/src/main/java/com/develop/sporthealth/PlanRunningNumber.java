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

public class PlanRunningNumber extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private MyLayout back;
    private Button save;
    private RadioGroup number;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;


    private static int target=0;
    private SPTools sp;
    private SQLOperator op;
    private Context context;

    private String date;

    private int tag;

    private boolean isData=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_running_number);

        init();

        getData();
    }

    private void init() {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);

        tag=getIntent().getIntExtra("tag",2);

        back=findViewById(R.id.number_back);
        save=findViewById(R.id.number_save);

        number =findViewById(R.id.number_rg);
        rb1 =findViewById(R.id.number_1);
        rb2 =findViewById(R.id.number_2);
        rb3 =findViewById(R.id.number_3);
        rb4 =findViewById(R.id.number_4);

        save.setOnClickListener(this);
        number.setOnCheckedChangeListener(this);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.number_save:
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

    private void getData() {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        data = op.select("select * from SportPlan where UserID=? and SportID<>1 and State=1", new String[]{sp.getID()});
        if (data.size() != 0) {
            isData = true;
            map = data.get(0);
            if(map.get("SportID").equals(tag+"")) {
                if (map.get("Target").equals("2")) {
                    rb1.setChecked(true);
                } else if (map.get("Target").equals("3")) {
                    rb2.setChecked(true);
                } else if (map.get("Target").equals("4")) {
                    rb3.setChecked(true);
                } else if (map.get("Target").equals("5")) {
                    rb4.setChecked(true);
                }
            }

        }else {
            AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
            query1.whereEqualTo("UserID",sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
            query2.whereNotEqualTo("SportID","1");
            AVQuery<AVObject> query3 = new AVQuery<>("SportPlan");
            query3.whereEqualTo("State","1");
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2,query3));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(list.size()>0) {
                        isData = true;
                        if(list.get(0).get("SportID").equals(tag+"")) {
                            if (list.get(0).get("Target").equals("2")) {
                                rb1.setChecked(true);
                            } else if (list.get(0).get("Target").equals("3")) {
                                rb2.setChecked(true);
                            } else if (list.get(0).get("Target").equals("4")) {
                                rb3.setChecked(true);
                            } else if (list.get(0).get("Target").equals("5")) {
                                rb4.setChecked(true);
                            }
                        }
                    }

                }
            });

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        setSelected();
        switch (checkedId){
            case R.id.number_1:
                rb1.setChecked(true);
                target=2;
                break;
            case R.id.number_2:
                rb2.setChecked(true);
                target=3;
                break;
            case R.id.number_3:
                rb3.setChecked(true);
                target=4;
                break;
            case R.id.number_4:
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

    public void insert(){
        date= TimeTools.getCurrentDate();
        if(isData){
            op.insert("update SportPlan set State=2,FinalDate=? where UserID=? and SportID<>1 and State=1",new String[]{date,sp.getID()});

            AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
            query1.whereEqualTo("UserID",sp.getID());
            AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
            query2.whereNotEqualTo("SportID","1");
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
        op.insert("insert into SportPlan(UserID,SportID,State,StartDate,Target) values(?,?,?,?,?)", new String[]{sp.getID(), tag + "", "1", date, target + ""});

        AVObject testObject1 = new AVObject("SportPlan");
        testObject1.put("UserID",sp.getID());
        testObject1.put("SportID", tag+"");
        testObject1.put("State","1");
        testObject1.put("Target", target+"");
        testObject1.put("StartDate",date);
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    Toast.makeText(context, "计划创建成功", Toast.LENGTH_SHORT).show();
                    AppManager.getAppManager().finishActivity(PlanRunning.class);
                    finish();
                }else {
                    Toast.makeText(context, "计划创建失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
