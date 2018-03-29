package com.develop.sporthealth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.develop.bean.Plans;
import com.develop.tools.AdapterTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/21.
 */

public class PlanMe extends AppCompatActivity {

    private MyLayout back;
    private ListView list;
    private int tag;
    private Context context;
    private SQLOperator op;
    private SPTools sp;

    private AdapterTools<Plans> adapterTools;
    private ArrayList<Plans> plansArray=new ArrayList<>();
    private Plans plans;

    private int j=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_me);

        init();
        getData();

    }

    private void init() {

        context=getApplicationContext();
        op=new SQLOperator(context);
        sp=new SPTools(context);

        tag=getIntent().getExtras().getInt("tag");

        back=findViewById(R.id.planme_back);
        list=findViewById(R.id.planme_list);

        if(tag==1){
            back.setText("正在进行");
        }else {
            back.setText("历史计划");
        }


        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        List<Map<String, String>> data = new ArrayList<>();
        //这句是为了从服务器中获取历史记录
        if(tag==2){
            tag=3;
        }
        data = op.select("select * from SportPlan a,SportInfo b where a.UserID=? and a.State=? and a.SportID=b.id", new String[]{sp.getID(),tag+""});
        if (data.size() != 0) {
            //查询本地数据库
            plansArray=new ArrayList<>();
            for(int i=0;i<data.size();i++){
                plans=new Plans();
                plans.setTitle(data.get(i).get("Name"));
                String plan="";
                if(data.get(i).get("SportID").equals("1"))
                {
                    plan="每天行走【"+plan+data.get(i).get("Target")+"】步";
                }else {
                    plan="每周【"+plan+data.get(i).get("Target")+"】次";
                }
                plans.setPlan(plan);
                plans.setSummary(data.get(i).get("Summary"));
                if (tag==1){
                    plans.setDate(data.get(i).get("StartDate"));
                }else {
                    plans.setDate(data.get(i).get("FinalDate"));
                }
                plansArray.add(plans);
            }
            adapterTools=new AdapterTools<Plans>(plansArray,R.layout.plan_me_item) {
                @Override
                public void bindView(ViewHolder holder, Plans obj) {
                    if(tag==1){
                        holder.setText(R.id.plan_item_txt,"开始时间：");
                    }else {
                        holder.setText(R.id.plan_item_txt,"结束时间：");
                    }
                    holder.setText(R.id.plan_item_title,obj.getTitle());
                    holder.setText(R.id.plan_item_state,obj.getPlan());
                    holder.setText(R.id.plan_item_summary,obj.getSummary());
                    holder.setText(R.id.plan_item_date,obj.getDate());
                }
            };

            list.setAdapter(adapterTools);

        }else {
            //这句是为了从服务器中获取历史记录
            if(tag==3){
                tag=2;
            }
            String cql = "select * from SportPlan where UserID='"+sp.getID()+"' and State='"+tag+"' order by FinalDate desc";
            AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        // 操作成功
                        List<AVObject> lists= (List<AVObject>) avCloudQueryResult.getResults();
                        plansArray = new ArrayList<>();
                        for (int i = 0; i < lists.size(); i++) {
                            plans = new Plans();
                            String plan = "";

                            List<Map<String, String>> data = new ArrayList<>();
                            data = op.select("select * from SportInfo where id=?", new String[]{lists.get(i).get("SportID").toString()});
                            if (data.size() != 0) {
                                plans.setTitle(data.get(0).get("Name"));
                                plans.setSummary(data.get(0).get("Summary"));
                            }
                            if (lists.get(i).get("SportID").equals("1")) {
                                plan = "每天行走【" + plan + lists.get(i).get("Target") + "】步";
                            } else {
                                plan = "每周【" + plan + lists.get(i).get("Target") + "】次";
                            }
                            plans.setPlan(plan);
                            plans.setSportID(lists.get(i).get("SportID").toString());

                            if (tag == 1) {
                                plans.setDate(lists.get(i).get("StartDate").toString());
                            } else {
                                if(lists.get(i).get("FinalDate")!=null) {
                                    plans.setDate(lists.get(i).get("FinalDate").toString());
                                }
                            }
                            plansArray.add(plans);
                        }
                        adapterTools=new AdapterTools<Plans>(plansArray,R.layout.plan_me_item) {
                            @Override
                            public void bindView(ViewHolder holder, Plans obj) {
                                if(tag==1){
                                    holder.setText(R.id.plan_item_txt,"开始时间：");
                                }else {
                                    holder.setText(R.id.plan_item_txt,"结束时间：");
                                }
                                holder.setText(R.id.plan_item_title,obj.getTitle());
                                holder.setText(R.id.plan_item_state,obj.getPlan());
                                holder.setText(R.id.plan_item_summary,obj.getSummary());
                                holder.setText(R.id.plan_item_date,obj.getDate());
                            }
                        };

                        list.setAdapter(adapterTools);
                    } else {
                        e.printStackTrace();
                    }
                }
            });

        }


    }


}
