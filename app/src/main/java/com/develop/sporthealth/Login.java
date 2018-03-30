package com.develop.sporthealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/3/16.
 */

public class Login extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private EditText user;
    private EditText pwd;

    private Button login;
    private Button cancel;
    private Button register;
    private Button find;

    private Context context;
    private SQLOperator op;
    private SPTools sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        context=getApplicationContext();

        op=new SQLOperator(context);
        sp=new SPTools(context);

        user=findViewById(R.id.login_username);
        pwd=findViewById(R.id.login_pwd);

        login=findViewById(R.id.login);
        cancel=findViewById(R.id.login_cancel);
        register=findViewById(R.id.register);
        find=findViewById(R.id.find);

        //文本变化的监听
        user.addTextChangedListener(this);
        pwd.addTextChangedListener(this);

        //按钮点击的监听
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        register.setOnClickListener(this);
        find.setOnClickListener(this);

        user.setText(sp.getUserName());
        pwd.setText(sp.getPWD());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                    public void done(AVException e) {
                        if (e == null) {
                            // 保存成功
                            final String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                            // 关联  installationId 到用户表等操作……
                            AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                            query.whereEqualTo("UserName", user.getText().toString());
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (list.size() > 0) {
                                        sp.setID(list.get(0).getObjectId());
                                        // 第一参数是 className,第二个参数是 objectId
                                        AVObject testObject1 = AVObject.createWithoutData("UserInfo", sp.getID());

                                        testObject1.put("InstallationId", installationId);

                                        // 保存到云端
                                        testObject1.saveInBackground();
                                        sp.setInstallationId(installationId);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // 保存失败，输出错误信息
                        }
                    }
                });
                AVQuery<AVObject> query1 = new AVQuery<>("UserInfo");
                query1.whereEqualTo("UserName",user.getText().toString());
                AVQuery<AVObject> query2 = new AVQuery<>("UserInfo");
                query2.whereEqualTo("Password",pwd.getText().toString());
                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list.size()>0) {
                            sp.setIsFirst(false);
                            sp.setID(list.get(0).getObjectId().toString());
                            sp.setUserName(list.get(0).get("UserName").toString());
                            sp.setPWD(list.get(0).get("Password").toString());
                            sp.setName(list.get(0).get("Name").toString());
                            if(list.get(0).get("ImageUrl")!=null) {
                                sp.setImage(list.get(0).get("ImageUrl").toString());
                            }
                            if(list.get(0).get("SendTime")!=null) {
                                sp.setDate(list.get(0).get("SendTime").toString());
                            }
                            //String str=list.get(0).get("Weight").toString();
                            if (!list.get(0).get("Weight").toString().equals("")) {
                                sp.setWeight(new Float(list.get(0).get("Weight").toString()).floatValue());
                            }
                            //设置登录标记
                            sp.setIsLogin(true);
                            //登录成功通知更新
                            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
                                sendBroadcast(new Intent("com.develop.sport.MYBROAD"));
                                sendBroadcast(new Intent("com.develop.sport.MYBROAD2"));
                            }
                            else {
                                sendBroadcast(new Intent("com.develop.sport.MYBROAD").setComponent(new ComponentName("com.develop.sporthealth", "com.develop.sporthealth.MeSy$MyBroad")));
                                sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth", "com.develop.sporthealth.InteractSy$MyBroad")));
                            }
                            finish();
                        }else {
                            //登录失败
                            Toast.makeText(context, "用户名或密码有误，请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.login_cancel:
                //取消
                finish();
                break;
            case R.id.register:
                //注册
                Intent it3=new Intent(context,Register.class);
                startActivity(it3);
                break;
            case R.id.find:
                //找回密码
                Intent it4=new Intent(context,ResetPWD.class);
                startActivity(it4);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        login.setEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(user.getText().length()>0&& pwd.getText().length()>0){
            login.setEnabled(true);
        }
    }
}
