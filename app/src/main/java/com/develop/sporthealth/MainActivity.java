package com.develop.sporthealth;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.tools.AppManager;
import com.develop.tools.CheckPermissionsTools;
import com.develop.tools.FragmentAdapter;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //页面管理
        AppManager.getAppManager().addActivity(MainActivity.this);

        mAdapter=new FragmentAdapter(getSupportFragmentManager(),MainActivity.this);
        bindView();
        rb_home.setChecked(true);


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



