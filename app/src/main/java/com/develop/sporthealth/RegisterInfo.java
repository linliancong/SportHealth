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
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.CodeTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/3/8.
 */

public class RegisterInfo extends AppCompatActivity implements View.OnClickListener{

    private EditText name;
    private EditText sex;
    private EditText email;
    private EditText weight;
    private EditText qq;
    private Button save;
    private MyLayout back;

    private SPTools sp;
    private SQLOperator op;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_info);

        init();
    }

    private void init() {
        context=RegisterInfo.this;
        sp=new SPTools(context);
        op=new SQLOperator(context);
        email=findViewById(R.id.info_email);
        name=findViewById(R.id.info_name);
        sex=findViewById(R.id.info_sex);
        weight=findViewById(R.id.info_weight);
        qq=findViewById(R.id.info_qq);
        save =findViewById(R.id.info_btn);
        back=findViewById(R.id.info_back);


        save.setOnClickListener(this);

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
            case R.id.info_btn:
                AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                query.whereEqualTo("UserName",sp.getUserName());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list.size()>0){
                            sp.setID(list.get(0).getObjectId());
                            // 第一参数是 className,第二个参数是 objectId
                            AVObject testObject1 = AVObject.createWithoutData("UserInfo", sp.getID());

                            testObject1.put("Name",name.getText().toString());
                            testObject1.put("Sex",sex.getText().toString());
                            testObject1.put("Email",email.getText().toString());
                            testObject1.put("QQ",qq.getText().toString());
                            testObject1.put("Weight",weight.getText().toString());
                            testObject1.put("Phone","");
                            // 保存到云端
                            testObject1.saveInBackground();
                            finish();
                        }
                    }
                });



                /*AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(final AVObject account, AVException e) {
                        account.put("Name",name.getText().toString()+"");
                        account.put("Sex",sex.getText().toString()+"");
                        account.put("Email",email.getText().toString()+"");
                        account.put("QQ",qq.getText().toString()+"");
                        account.put("Weight",weight.getText().toString()+"");
                        account.put("Phone","");
                        AVSaveOption option = new AVSaveOption();
                        option.query(new AVQuery<>("UserInfo").whereEqualTo("UserName", sp.getUserName()));
                        option.setFetchWhenSave(true);
                        account.saveInBackground(option, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(context,"保存失败，请稍后重试",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });*/

                break;

        }
    }
}
