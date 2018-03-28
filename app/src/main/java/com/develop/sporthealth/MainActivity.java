package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.develop.bean.StepEntity;
import com.develop.tools.AppManager;
import com.develop.tools.CheckPermissionsTools;
import com.develop.tools.FragmentAdapter;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;


/**
 * Created by Administrator on 2017/3/16.
 */

public class MainActivity extends CheckPermissionsTools implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{

    public static final int PAG_ONE=0;
    public static final int PAG_TWO=1;
    public static final int PAG_THREE=2;
    public static final int PAG_FOUR=3;

    private RadioGroup rg_tab_bar;
    private RadioButton rb_home;
    private RadioButton rb_plan;
    private RadioButton rb_interact;
    private RadioButton rb_me;


    private View view_home;
    private View view_plan;
    private View view_interact;
    private View view_me;
    private TextView txt_topbar;

    private ViewPager vpager;
    private FragmentAdapter mAdapter=null;

    private long mTime=0;
    private Context context;
    private SPTools sp;
    private SQLOperator op;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //页面管理
        AppManager.getAppManager().addActivity(MainActivity.this);

        context=getApplicationContext();
        sp=new SPTools(context);
        op=new SQLOperator(context);

        //这里初始化运动计划
        if(sp.getIsFirst()){
            op.insert("insert into SportInfo(Name,Summary) values(?,?)",new String[]{"步行","这是最简单的运动方式了，每天适量的运动就可以使我们保持健康。"});
            op.insert("insert into SportInfo(Name,Summary) values(?,?)",new String[]{"5公里跑步","适当的跑跑步，增强自己的体魄，让自己可以更好的适应生活。"});
            op.insert("insert into SportInfo(Name,Summary) values(?,?)",new String[]{"10公里跑步","跑步是一项长期的运动，老少皆宜，十公里说长不长，说短也不短。"});
            op.insert("insert into SportInfo(Name,Summary) values(?,?)",new String[]{"半程马拉松","半程马拉松适合那些普通跑步已经不能满足需求的人，也适合那些想要参加马拉松的人，这只是入门。"});
            op.insert("insert into SportInfo(Name,Summary) values(?,?)",new String[]{"马拉松","如果你可以坚持着跑下来，那么不就的将来你就是马拉松冠军的热门人选了，加油！"});
            sp.setIsFirst(false);
        }

        /*//消息表
        AVObject testObject1 = new AVObject("Messages");
        testObject1.put("UserID", "");
        testObject1.put("Date", "");
        testObject1.put("State","");
        testObject1.put("Title","");
        testObject1.put("Content","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //步数表
        testObject1 = new AVObject("Step");
        testObject1.put("UserID","");
        testObject1.put("Date", "");
        testObject1.put("TotalSteps","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //分享信息表
        testObject1 = new AVObject("Share");
        testObject1.put("UserID","");
        testObject1.put("Date", "");
        testObject1.put("Title","");
        testObject1.put("Content", "");
        testObject1.put("Count","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //运动完成情况表
        testObject1 = new AVObject("SportFinish");
        testObject1.put("UserID","");
        testObject1.put("Time", "");
        testObject1.put("SportID","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //跑步统计表
        testObject1 = new AVObject("SportRunning");
        testObject1.put("UserID","");
        testObject1.put("SportID", "");
        testObject1.put("Total","");
        testObject1.put("Time", "");
        testObject1.put("Speed","");
        testObject1.put("Hot","");
        testObject1.put("StartTime", "");
        testObject1.put("EndTime","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //运动轨迹表
        testObject1 = new AVObject("SportLocation");
        testObject1.put("UserID","");
        testObject1.put("RunID", "");
        testObject1.put("Longitude","");
        testObject1.put("Latitude", "");
        testObject1.put("Speed","");
        testObject1.put("Time","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });
        //运动轨迹表
        testObject1 = new AVObject("SportPlan");
        testObject1.put("UserID","");
        testObject1.put("SportID", "");
        testObject1.put("State","");
        testObject1.put("Target", "");
        testObject1.put("StartDate","");
        testObject1.put("FinalDate","");
        testObject1.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
            }
        });*/

        mAdapter=new FragmentAdapter(getSupportFragmentManager(),MainActivity.this);
        bindView();
        rb_home.setChecked(true);

        //这里是测试数据
        /*op.insert("insert into SportFinish(UserID,SportID,Time) values(?,?,?)",new String[]{sp.getID(),"1", TimeTools.getCurrentDate()});
        op.insert("insert into SportFinish(UserID,SportID,Time) values(?,?,?)",new String[]{sp.getID(),"5", TimeTools.getCurrentDate()});
        op.insert("insert into SportFinish(UserID,SportID,Time) values(?,?,?)",new String[]{sp.getID(),"5", "2018年3月19日"});*/
        //op.insert("delete from SportFinish",new String[]{});



    }

    public void bindView() {
        //设置菜单上方的区块
        view_home=findViewById(R.id.view_home);
        view_plan =findViewById(R.id.view_plan);
        view_interact =findViewById(R.id.view_interact);
        view_me=findViewById(R.id.view_me);

        //顶部标题栏
        txt_topbar= findViewById(R.id.txt_topbar);

        //按钮
        rg_tab_bar=  findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);
        //获取第一个按钮，并设置其状态为选中
        rb_home= findViewById(R.id.rb_home);
        rb_plan = findViewById(R.id.rb_plan);
        rb_interact = findViewById(R.id.rb_interact);
        rb_me= findViewById(R.id.rb_me);

        //viewPager相关的设置
        vpager= findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_home:
                txt_topbar.setText("首页");
                setSelected();
                view_home.setSelected(true);
                vpager.setCurrentItem(PAG_ONE);
                break;
            case R.id.rb_plan:
                txt_topbar.setText("计划");
                setSelected();
                view_plan.setSelected(true);
                vpager.setCurrentItem(PAG_TWO);
                break;
            case R.id.rb_interact:
                txt_topbar.setText("互动");
                setSelected();
                view_interact.setSelected(true);
                vpager.setCurrentItem(PAG_THREE);
                break;
            case R.id.rb_me:
                txt_topbar.setText("个人中心");
                setSelected();
                view_me.setSelected(true);
                vpager.setCurrentItem(PAG_FOUR);
                break;
            default:break;
        }
    }

    public void setSelected(){
        view_home.setSelected(false);
        view_plan.setSelected(false);
        view_me.setSelected(false);
        view_interact.setSelected(false);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state状态有3个，0：什么都没做，1：正在滑动，2：滑动完毕
        // 由于ViewPager 放在 RadioButton 后，所以RadioButton 的点击事件会失效。
        if (state==2)
        {
            switch (vpager.getCurrentItem()){
                case PAG_ONE:
                    rb_home.setChecked(true);
                    break;
                case PAG_TWO:
                    rb_plan.setChecked(true);
                    break;
                case PAG_THREE:
                    rb_interact.setChecked(true);
                    break;
                case PAG_FOUR:
                    rb_me.setChecked(true);
                    break;
            }

        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-mTime>2000)
        {
            Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
            mTime=System.currentTimeMillis();
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}



