package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/3/16.
 */

public class PlanSy extends Fragment implements View.OnClickListener{

    private Context context;
    private View view;

    private RelativeLayout walk;
    private RelativeLayout running;
    private RelativeLayout unfinished;
    private RelativeLayout finish;
    private RelativeLayout remind;
    private TextView remind_v;

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

            walk=view.findViewById(R.id.plan_walk);
            running=view.findViewById(R.id.plan_running);
            unfinished=view.findViewById(R.id.plan_unfinished);
            finish=view.findViewById(R.id.plan_finish);
            remind=view.findViewById(R.id.plan_remind);
            remind_v=view.findViewById(R.id.plan_remind_v);

            walk.setOnClickListener(this);
            running.setOnClickListener(this);
            unfinished.setOnClickListener(this);
            finish.setOnClickListener(this);
            remind.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                Intent intent5=new Intent(context,PlanRemind.class);
                startActivity(intent5);
                break;
        }
    }
}
