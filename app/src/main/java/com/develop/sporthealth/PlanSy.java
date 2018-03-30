package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class PlanSy extends Fragment implements View.OnClickListener{

    private Context context;
    private View view;
    private SQLOperator op;
    private SPTools sp;

    private RelativeLayout sign;
    private RelativeLayout statistics;
    private RelativeLayout walk;
    private RelativeLayout running;
    private RelativeLayout unfinished;
    private RelativeLayout finish;
    private RelativeLayout remind;
    private TextView remind_v;

    private ShowBack showBack;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    //op.insert("update Messages set State=2 where Date=? and UserID=?", new String[]{TimeTools.getCurrentDate(),sp.getID()});
                    AVQuery<AVObject> query1 = new AVQuery<>("Messages");
                    query1.whereEqualTo("Date",TimeTools.getCurrentDate());
                    AVQuery<AVObject> query2 = new AVQuery<>("Messages");
                    query2.whereEqualTo("UserID",sp.getID());
                    AVQuery<AVObject> query3 = new AVQuery<>("Messages");
                    query3.whereEqualTo("State","1");
                    AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2,query3));
                    query.findInBackground(new FindCallback<AVObject>() {
                           @Override
                           public void done(List<AVObject> list, AVException e) {
                               if(list.size()>0) {
                                   // 第一参数是 className,第二个参数是 objectId
                                   AVObject testObject1 = AVObject.createWithoutData("Messages", list.get(0).getObjectId());
                                   testObject1.put("State", "2");
                                   // 保存到云端
                                   testObject1.saveInBackground();

                                   getVisible();
                                   showBack= (ShowBack) getActivity();
                                   showBack.callBack(0x001);


                               }

                           }
                       });
                    break;
                case 0x002:
                    showBack= (ShowBack) getActivity();
                    showBack.callBack(0x002);
                    break;
                case 0x003:
                    showBack= (ShowBack) getActivity();
                    showBack.callBack(0x001);
                    break;
            }
        }
    };

    public PlanSy(){}
    @SuppressLint("ValidFragment")
    public PlanSy(Context context){
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.plan, container, false);

            init();
        }
        if(sp.getIsLogin()) {
            getData();
        }
        return view;
    }

    private void getData() {
        //每次都从服务器把最新的计划同步下来
        op.insert("delete from SportPlan where UserID=? and State=1 ", new String[]{sp.getID()});
        AVQuery<AVObject> query1 = new AVQuery<>("SportPlan");
        query1.whereEqualTo("UserID", sp.getID());
        AVQuery<AVObject> query2 = new AVQuery<>("SportPlan");
        query2.whereEqualTo("State", "1");
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        op.insert("insert into SportPlan(UserID,SportID,State,StartDate,Target) values(?,?,?,?,?)", new String[]{sp.getID(),
                                list.get(i).get("SportID").toString(), list.get(i).get("State").toString(),
                                list.get(i).get("StartDate").toString(), list.get(i).get("Target").toString()});
                                getVisible();
                    }

                }
            }
        });
    }

    private void getVisible() {
        //先判断有没有数据，没有的话加入;有的话判断是否已读
        AVQuery<AVObject> query1 = new AVQuery<>("Messages");
        query1.whereEqualTo("UserID",sp.getID());
        AVQuery<AVObject> query2 = new AVQuery<>("Messages");
        query2.whereEqualTo("Date",TimeTools.getCurrentDate());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()==0) {
                    AVObject testObject1 = new AVObject("Messages");
                    testObject1.put("UserID",sp.getID());
                    testObject1.put("Date",TimeTools.getCurrentDate());
                    testObject1.put("State","1");
                    testObject1.put("Title","计划提醒");
                    testObject1.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                                remind_v.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(0x002);
                            }else {
                                remind_v.setVisibility(View.GONE);
                                handler.sendEmptyMessage(0x003);
                            }
                        }
                    });

                }else{
                    if(list.get(0).get("State").equals("1")){
                        remind_v.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(0x002);
                    }else {
                        remind_v.setVisibility(View.GONE);
                        handler.sendEmptyMessage(0x003);
                    }
                }
            }
        });
        /*List<Map<String, String>> data = new ArrayList<>();
        data = op.select("select count(1) num from Messages where UserID=? and Date=?", new String[]{sp.getID(), TimeTools.getCurrentDate()});
        if (data.size() != 0) {
            if(!data.get(0).get("num").equals("1")){
                op.insert("insert into Messages(UserID,Date,State,Title) values(?,?,1,?)", new String[]{sp.getID(),TimeTools.getCurrentDate(),"计划提醒"});
            }
        }*/

        //判断今天的消息是否未读
        /*query1 = new AVQuery<>("Messages");
        query1.whereEqualTo("Date",TimeTools.getCurrentDate());
        query2 = new AVQuery<>("Messages");
        query2.whereEqualTo("UserID",sp.getID());
        AVQuery<AVObject> query3 = new AVQuery<>("Messages");
        query3.whereEqualTo("State","1");
        query = AVQuery.and(Arrays.asList(query1, query2,query3));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0) {
                    remind_v.setVisibility(View.VISIBLE);
                }else {
                    remind_v.setVisibility(View.GONE);
                }
            }
        });*/
            /*data = op.select("select count(1) num from Messages where UserID=? and State=1 and Date=?", new String[]{sp.getID(), TimeTools.getCurrentDate()});
        if (data.size() != 0) {
            if(data.get(0).get("num").equals("1")){
                remind_v.setVisibility(View.VISIBLE);
            }else {
                remind_v.setVisibility(View.GONE);
            }
        }*/
    }

    private void init() {

        op=new SQLOperator(context);
        sp=new SPTools(context);
        sign=view.findViewById(R.id.plan_sign);
        statistics=view.findViewById(R.id.plan_statistics);
        walk=view.findViewById(R.id.plan_walk);
        running=view.findViewById(R.id.plan_running);
        unfinished=view.findViewById(R.id.plan_unfinished);
        finish=view.findViewById(R.id.plan_finish);
        remind=view.findViewById(R.id.plan_remind);
        remind_v=view.findViewById(R.id.plan_remind_v);

        sign.setOnClickListener(this);
        statistics.setOnClickListener(this);
        walk.setOnClickListener(this);
        running.setOnClickListener(this);
        unfinished.setOnClickListener(this);
        finish.setOnClickListener(this);
        remind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.plan_sign:
                Intent intent11=new Intent(context,PlanSing.class);
                startActivity(intent11);
                break;
            case R.id.plan_statistics:
                Intent intent22=new Intent(context,PlanStatistics.class);
                startActivity(intent22);
                break;
            case R.id.plan_running:
                Intent intent1=new Intent(context,PlanRunning.class);
                startActivity(intent1);
                break;
            case R.id.plan_walk:
                Intent intent2=new Intent(context,PlanWalk.class);
                startActivity(intent2);
                break;
            case R.id.plan_unfinished:
                Intent intent3=new Intent(context,PlanMe.class);
                intent3.putExtra("tag",1);
                startActivity(intent3);
                break;
            case R.id.plan_finish:
                Intent intent4=new Intent(context,PlanMe.class);
                intent4.putExtra("tag",2);
                startActivity(intent4);
                break;
            case R.id.plan_remind:
                handler.sendEmptyMessage(0x001);
                Intent intent5=new Intent(context,PlanRemind.class);
                startActivity(intent5);
                break;
        }
    }

    public interface ShowBack {
        public void callBack(int result);
    }

}
