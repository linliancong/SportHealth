package com.develop.sporthealth;

import android.content.Context;
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

import com.develop.tools.CodeTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
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
                        List<Map<String, String>> data = new ArrayList<>();
                        Map<String, String> map = new HashMap<>();
                        data = op.select("select count(1) num from UserInfo where UserName=?", new String[]{user.getText().toString()});
                        if (data.size() != 0) {
                            map = data.get(0);
                            if (map.get("num").toString().equals("1")) {
                                Toast.makeText(context, "用户名已存在，重新输入用户名", Toast.LENGTH_SHORT).show();
                                imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                                realCode = CodeTools.getInstance().getCode().toLowerCase();
                            } else {
                                //判断邮箱
                                data = new ArrayList<>();
                                map = new HashMap<>();
                                data = op.select("select count(1) num from UserInfo where Email=?", new String[]{email.getText().toString()});
                                if (data.size() != 0) {
                                    map = data.get(0);
                                    if (map.get("num").toString().equals("1")) {
                                        Toast.makeText(context, "邮箱已存在，重新输入邮箱或者找回密码", Toast.LENGTH_SHORT).show();
                                        imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                                        realCode = CodeTools.getInstance().getCode().toLowerCase();
                                    } else {
                                        op.insert("insert into UserInfo(UserName,Password,Email,Name) values(?,?,?,?)", new String[]{user.getText().toString(),
                                                pwd1.getText().toString(), email.getText().toString(), user.getText().toString()});
                                        Toast.makeText(context, "注册成功！返回重新登录", Toast.LENGTH_SHORT).show();
                                        /*Intent intent = new Intent(context, Login.class);
                                        startActivity(intent);*/
                                        finish();
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(context, "两次输入的密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
                            imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                            realCode = CodeTools.getInstance().getCode().toLowerCase();
                        }
                    } else {
                        Toast.makeText(context, "验证码输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                        imgCode.setImageBitmap(CodeTools.getInstance().createBitmap());
                        realCode = CodeTools.getInstance().getCode().toLowerCase();
                    }
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
