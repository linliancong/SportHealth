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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSMS;
import com.avos.avoscloud.AVSMSOption;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.List;


/**
 * Created by Administrator on 2018/3/8.
 */

public class ResetPWD extends AppCompatActivity implements View.OnClickListener,TextWatcher {

    private EditText phone;
    private EditText pwd1;
    private EditText pwd2;
    private EditText code;
    private Button find;
    private MyLayout back;
    private Button sendCode;

    private SPTools sp;
    private SQLOperator op;
    private Context context;

    private String id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);

        init();
    }

    private void init() {
        context=ResetPWD.this;
        sp=new SPTools(context);
        op=new SQLOperator(context);
        phone =findViewById(R.id.reset_phone);
        pwd1=findViewById(R.id.reset_pwd1);
        pwd2=findViewById(R.id.reset_pwd2);
        code=findViewById(R.id.reset_code);
        find=findViewById(R.id.reset_btn);
        back=findViewById(R.id.reset_back);
        sendCode=findViewById(R.id.reset_send);

        find.setOnClickListener(this);
        sendCode.setOnClickListener(this);
        phone.addTextChangedListener(this);
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
            case R.id.reset_btn:
                //判断密码
                if (pwd1.getText().toString().equals(pwd2.getText().toString())) {
                    AVSMS.verifySMSCodeInBackground(code.getText().toString(), phone.getText().toString(), new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            if (null == e) {
                                AVObject testObject1 = AVObject.createWithoutData("UserInfo", sp.getID());
                                testObject1.put("Password", pwd1.getText().toString());
                                // 保存到云端
                                //testObject1.saveInBackground();
                                testObject1.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if(e == null){
                                            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(context, "修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "验证码输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(context, "两次输入的密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset_send:
                //判断手机
                AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                query.whereEqualTo("Phone", phone.getText().toString());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list.size() == 0) {
                            Toast.makeText(context, "手机不存在，请重新输入", Toast.LENGTH_SHORT).show();
                        } else {
                            id=list.get(0).getObjectId();
                            AVSMSOption option = new AVSMSOption();
                            option.setTtl(10);                     // 验证码有效时间为 10 分钟
                            option.setApplicationName("SportHealth");
                            option.setOperation("找回密码");
                            AVSMS.requestSMSCodeInBackground(phone.getText().toString(), option, new RequestMobileCodeCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (null == e) {
                                        Toast.makeText(context, "验证码发送成功，请注意查收", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "验证码发送失败，请检查你的手机号是否正确", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        find.setEnabled(false);
        sendCode.setEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(pwd1.getText().length()>0&&pwd2.getText().length()>0&& phone.getText().length()>0&&code.getText().length()>0){
            find.setEnabled(true);
        }
        if(phone.getText().length()>0){
            sendCode.setEnabled(true);
        }

    }
}
