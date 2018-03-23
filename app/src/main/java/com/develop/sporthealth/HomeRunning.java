package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.develop.bean.Running;
import com.develop.tools.AdapterTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.io.Serializable;
import java.util.ArrayList;
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

        setAdapter();
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
        int speed=0;
        int S=0;
        int M=0;
        List<Map<String, String>> data = new ArrayList<>();
        data = op.select("select * from SportRunning where UserID=? order by StartTime desc", new String[]{sp.getID()});
        if (data.size() != 0) {
            for (int i=0;i<data.size();i++) {
                running=new Running();
                running.setRunID(data.get(i).get("id"));
                running.setStartTime(data.get(i).get("StartTime"));
                running.setEndTime(data.get(i).get("EndTime"));
                running.setTime(data.get(i).get("Time"));
                running.setHot(data.get(i).get("Hot"));
                running.setTotal(data.get(i).get("Total"));
                if(data.get(i).get("Speed")!=null){
                    speed=new Integer(data.get(i).get("Speed"));
                    S=speed%60;
                    M=speed/60;
                    running.setSpeed(M + "'" + S + "''");
                }else{
                    running.setSpeed("--");
                }
                runnings.add(running);
            }

        }
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
