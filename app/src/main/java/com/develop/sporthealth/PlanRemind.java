package com.develop.sporthealth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/21.
 */

public class PlanRemind extends AppCompatActivity {
    private MyLayout back;
    private TextView walk;
    private TextView running;

    private Context context;
    private SQLOperator op;
    private SPTools sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_remind);

        init();
        getData();


    }

    private void init() {
        context=getApplicationContext();
        op=new SQLOperator(context);
        sp=new SPTools(context);
        back=findViewById(R.id.remind_back);
        walk=findViewById(R.id.remind_walk);
        running=findViewById(R.id.remind_running);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        List<Map<String, String>> data = new ArrayList<>();
        //判断步数
        data = op.select("select count(*) num from SportFinish where UserID=? and SportID=1 and Time=?", new String[]{sp.getID(), TimeTools.getCurrentDate()});
        if (data.size() != 0) {
            if(data.get(0).get("num").equals("1")){
                walk.setText("恭喜你已经完成今天的步数目标！继续努力呦~");
            }else {
                data= op.select("select a.Target,b.TotalSteps from SportPlan a,Step b where a.SportID=1 and a.State=1 and b.CurDate=?", new String[]{TimeTools.getCurrentDate()});
                if (data.size() != 0) {
                    int targer=new Integer(data.get(0).get("Target")).intValue();
                    int step=new Integer(data.get(0).get("TotalSteps")).intValue();
                    if(step<targer){
                        walk.setText("今日步数 【"+step+"】 步，还差 【"+(targer-step)+"】 步完成目标，继续加油哦！");
                    }else
                    {
                        walk.setText("恭喜你已经完成今天的步数目标！继续努力呦~");
                    }
                }else {
                    walk.setText("你还没有步数目标！");
                }
            }
        }

        //判断运动
        data = op.select("select count(*) num from SportFinish where UserID=? and SportID<>1 and Time>=?", new String[]{sp.getID(), TimeTools.getCurWeekMon()});
        if (data.size() != 0) {
            int count=new Integer(data.get(0).get("num")).intValue();
            data = op.select("select * from SportPlan a,SportInfo b where a.UserID=? and a.State=1 and a.SportID=b.id and a.SportID<>1", new String[]{sp.getID()});
            if (data.size() != 0) {
                int targer=new Integer(data.get(0).get("Target")).intValue();
                String name=data.get(0).get("Name");
                if(count<targer){
                    running.setText("本周完成 【"+name+"】 跑步 【"+count+"】 次，还差 【"+(targer-count)+"】 次，马上就要达到目标了，请继续坚持，生活总会把你想要的还给你的！");
                }else
                {
                    running.setText("恭喜你已经完成本周的跑步目标！继续努力呦~");
                }
            }else{
                running.setText("你还没有跑步目标！");
            }
        }


    }
}
