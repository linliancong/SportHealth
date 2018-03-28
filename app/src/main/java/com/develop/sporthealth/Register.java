package com.develop.sporthealth;

import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.CodeTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/8.
 */

public class Register extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private EditText email;
    private EditText user;
    private EditText pwd1;
    private EditText pwd2;
    private EditText code;
    private Button regi;
    private MyLayout back;
    private ImageView imgCode;

    private SPTools sp;
    private SQLOperator op;
    private Context context;

    //生成的验证码
    private String realCode;

    private String cql="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        init();
    }

    private void init() {
        context=Register.this;
        sp=new SPTools(context);
        op=new SQLOperator(context);
        email=findViewById(R.id.regi_email);
        user=findViewById(R.id.regi_username);
        pwd1=findViewById(R.id.regi_pwd1);
        pwd2=findViewById(R.id.regi_pwd2);
        code=findViewById(R.id.regi_code);
        regi=findViewById(R.id.regi_btn);
        back=findViewById(R.id.regi_back);
        imgCode=findViewById(R.id.regi_imgcode);
        imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
        realCode=CodeTools.getInstance().getCode().toLowerCase();

        regi.setOnClickListener(this);
        imgCode.setOnClickListener(this);
        email.addTextChangedListener(this);
        user.addTextChangedListener(this);
        pwd1.addTextChangedListener(this);
        pwd2.addTextChangedListener(this);
        code.addTextChangedListener(this);

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
            case R.id.regi_btn:
                //判断验证码
                if (realCode.equals(code.getText().toString())) {
                    //判断密码
                    if (pwd1.getText().toString().equals(pwd2.getText().toString())) {
                        //判断用户名
                        AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                        query.whereEqualTo("UserName",user.getText().toString());
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if(list.size()>0){
                                    Toast.makeText(context, "用户名已存在，重新输入用户名", Toast.LENGTH_SHORT).show();
                                    imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                                    realCode = CodeTools.getInstance().getCode().toLowerCase();
                                }else {
                                    AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                                    query.whereEqualTo("Email",email.getText().toString());
                                    query.findInBackground(new FindCallback<AVObject>() {
                                        @Override
                                        public void done(List<AVObject> list, AVException e) {
                                            if(list.size()>0){
                                                Toast.makeText(context, "邮箱已存在，重新输入邮箱或者找回密码", Toast.LENGTH_SHORT).show();
                                                imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                                                realCode = CodeTools.getInstance().getCode().toLowerCase();
                                            }else {
                                                AVObject testObject1 = new AVObject("UserInfo");
                                                testObject1.put("UserName",user.getText().toString());
                                                testObject1.put("Password",pwd1.getText().toString());
                                                testObject1.put("Email",email.getText().toString());
                                                testObject1.put("Name",user.getText().toString());
                                                testObject1.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if(e == null){
                                                            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                                                            sp.setUserName(user.getText().toString());
                                                            Intent intent=new Intent(context,RegisterInfo.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }else{
                                                            Toast.makeText(context, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }else {
                            Toast.makeText(context, "两次输入的密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
                            imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                            realCode = CodeTools.getInstance().getCode().toLowerCase();
                        }
                    } else {
                        Toast.makeText(context, "验证码输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                        imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                        realCode = CodeTools.getInstance().getCode().toLowerCase();
                    }
                break;
            case R.id.regi_imgcode:
                imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                realCode=CodeTools.getInstance().getCode().toLowerCase();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        regi.setEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(user.getText().length()>0&&pwd1.getText().length()>0&&pwd2.getText().length()>0&&email.getText().length()>0&&code.getText().length()>0){
            regi.setEnabled(true);
        }

    }
}
