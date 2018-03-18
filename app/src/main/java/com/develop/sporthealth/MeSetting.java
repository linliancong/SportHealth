package com.develop.sporthealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MeSetting extends AppCompatActivity implements View.OnClickListener{

    private Context context;
    private RelativeLayout pwd;
    private RelativeLayout exit;
    private RelativeLayout gy;
    private MyLayout back;

    private SPTools sp;

    //弹窗所需的控件
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_setting);

        context=MeSetting.this;
        pwd=findViewById(R.id.setting_pwd);
        exit=findViewById(R.id.setting_exit);
        gy=findViewById(R.id.setting_gy);
        back=findViewById(R.id.setting_back);

        sp=new SPTools(context);

        pwd.setOnClickListener(this);
        exit.setOnClickListener(this);
        gy.setOnClickListener(this);

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
            case R.id.setting_pwd:
                if(sp.getIsLogin()) {
                    View ad_view2 = getAlert(R.layout.ad_input_pass);
                    final EditText editText = (EditText) ad_view2.findViewById(R.id.ad_edit_pass);
                    ad_view2.findViewById(R.id.ad_btn_pass_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //handler.sendEmptyMessage(0x0001);
                            alert.dismiss();
                        }
                    });
                    ad_view2.findViewById(R.id.ad_btn_pass_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //handler.sendEmptyMessage(0x0002);
                            SPTools sp = new SPTools(context);
                            if (sp.getPWD().equals(editText.getText().toString())) {
                                //原密码正确的操作
                                Intent intent = new Intent(context, MePwd.class);
                                startActivity(intent);
                                finish();
                                alert.dismiss();
                            } else {
                                alert.dismiss();
                                View view = getAlert(R.layout.ad_pass_erro);
                                TextView txt = (TextView) view.findViewById(R.id.ad_txt_erro2);
                                //String name=editText.getText().toString();
                                if (editText.getText().toString().equals("")) {
                                    txt.setText("原密码不能为空。");
                                }
                                view.findViewById(R.id.ad_btn_erro_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.setting_exit:
                if(sp.getIsLogin()) {
                    sp.setIsLogin(false);
                    sendBroadcast(new Intent("com.develop.sport.MYBROAD"));
                    finish();
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.setting_gy:
                break;
        }

    }

    //定义弹窗方法
    public View getAlert(int mLayout){
        View ad_view;
        //初始化Builder
        builder=new AlertDialog.Builder(context);
        //完成相关设置
        //inflater=getLayoutInflater();
        inflater=LayoutInflater.from(context);
        ad_view=inflater.inflate(mLayout,null,false);
        builder.setView(ad_view);
        builder.setCancelable(true);
        alert=builder.create();
        alert.show();
        return ad_view;
    }
}
