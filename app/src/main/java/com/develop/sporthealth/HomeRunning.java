package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.develop.bean.Running;
import com.develop.tools.AdapterTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/23.
 */

public class HomeRunning extends AppCompatActivity {
    private MyLayout back;
    private ListView list;

    private Context context;
    private SQLOperator op;
    private SPTools sp;

    private AdapterTools<Running> adapterTools;
    private ArrayList<Running> runnings=new ArrayList<>();
    private Running running=new Running();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_running);

        init();
        getData();

    }

    private void init() {
        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);
        back=findViewById(R.id.homerun_back);
        list=findViewById(R.id.homerun_list);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        final int[] speed = {0};
        final int[] time = {0};
        final int[] S = {0};
        final int[] M = {0};
        final int[] H = {0};
        final int[] HMS = {0};
        //显示统计数据
        AVQuery<AVObject> query = new AVQuery<>("SportRunning");
        query.whereEqualTo("UserID",sp.getID());
        query.orderByDescending("StartTime");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                    String date1 = "";
                    String date2 = "";
                    int i = 0;
                    double total = 0;
                    for (; i < list.size(); i++) {
                        running = new Running();
                        running.setRunID(list.get(i).getObjectId());
                        running.setStartTime(list.get(i).get("StartTime").toString());
                        running.setEndTime(list.get(i).get("EndTime").toString());
                        running.setHot(list.get(i).get("Hot").toString());
                        running.setTotal(list.get(i).get("Total").toString());
                        if (list.get(i).get("Speed") != null) {
                            speed[0] = new Integer(list.get(i).get("Speed").toString());
                            S[0] = speed[0] % 60;
                            M[0] = speed[0] / 60;
                            running.setSpeed(M[0] + "'" + S[0] + "''");
                        } else {
                            running.setSpeed("--");
                        }
                        time[0] = new Integer(list.get(i).get("Time").toString());
                        S[0] = time[0] % 60;
                        HMS[0] = time[0] / 60;
                        M[0] = HMS[0] % 60;
                        H[0] = HMS[0] / 60;
                        running.setTime((H[0] >= 10 ? (H[0] + "") : ("0" + H[0])) + ":" + (M[0] >= 10 ? (M[0] + "") : ("0" + M[0])) + ":" + (S[0] >= 10 ? (S[0] + "") : ("0" + S[0])));
                        runnings.add(running);
                    }
                    setAdapter();
                }else {
                    Toast.makeText(context,"没有数据",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setAdapter() {
        adapterTools=new AdapterTools<Running>(runnings, R.layout.home_running_item) {
            @Override
            public void bindView(ViewHolder holder, Running obj) {
                holder.setText(R.id.homerun_item_start,obj.getStartTime());
                holder.setText(R.id.homerun_item_end,obj.getEndTime());
                holder.setText(R.id.homerun_total,obj.getTotal());
                holder.setText(R.id.homerun_time,obj.getTime());
                holder.setText(R.id.homerun_speed,obj.getSpeed());
                holder.setText(R.id.homerun_hot,obj.getHot());

            }
        };

        list.setAdapter(adapterTools);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,HomeRunningShow.class);
                intent.putExtra("RunID",runnings.get(position).getRunID());
                startActivity(intent);
            }
        });
    }

}
