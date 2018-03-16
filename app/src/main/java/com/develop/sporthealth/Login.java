package com.develop.sporthealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
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

        //文本变化的监听
        user.addTextChangedListener(this);
        pwd.addTextChangedListener(this);

        //按钮点击的监听
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                List<Map<String, String>> data = new ArrayList<>();
                Map<String, String> map = new HashMap<>();
                data = op.select("select * from UserInfo where UserName=? and Password=?", new String[]{user.getText().toString(), pwd.getText().toString()});
                if (data.size()!=0) {
                    map = data.get(0);
                    if (map.get("UserName").toString().equals(user.getText().toString())) {
                        //登录成功
                        sp.setIsFirst(false);
                        sp.setID(map.get("id"));
                        sp.setUserName(map.get("UserName"));
                        sp.setPWD(map.get("Password"));
                        sp.setName(map.get("Name"));
                        //设置登录标记
                        sp.setIsLogin(true);
                        //登录成功通知更新
                        sendBroadcast(new Intent("com.develop.sport.MYBROAD"));
                        finish();
                    } else {
                        //登录失败
                        Toast.makeText(context, "用户名或密码有误，请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                }
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
