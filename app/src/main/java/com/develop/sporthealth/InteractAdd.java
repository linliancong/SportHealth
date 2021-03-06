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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/27.
 */

public class InteractAdd extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private MyLayout back;
    private Button send;
    private EditText content;

    private Context context;
    private SQLOperator op;
    private SPTools sp;
    private String time="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interact_add);

        context=getApplicationContext();
        op=new SQLOperator(context);
        sp=new SPTools(context);

        back=findViewById(R.id.add_back);
        send=findViewById(R.id.add_send);
        content=findViewById(R.id.add_content);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(this);

        content.addTextChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_send:
                time = TimeTools.getCurrentDate2();
                //这里新增一条数据传到服务器
                //从服务器中获取数据
                AVQuery<AVObject> query = new AVQuery<>("SportFinish");
                query.whereEqualTo("UserID", sp.getID());
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, AVException e) {
                        if (e == null) {
                            AVObject testObject1 = new AVObject("Share");
                            testObject1.put("UserID",sp.getID());
                            testObject1.put("UserName",sp.getUserName());
                            testObject1.put("Title","健身心得与知识");
                            testObject1.put("Content",content.getText().toString());
                            testObject1.put("Date",time);
                            testObject1.put("Count","0");
                            testObject1.put("ImageUrl",sp.getImage());
                            testObject1.put("Rank",i);
                            testObject1.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e==null){
                                        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                                        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
                                            sendBroadcast(new Intent("com.develop.sport.MYBROAD2"));
                                        }
                                        else {
                                            sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth", "com.develop.sporthealth.InteractSy$MyBroad")));
                                        }
                                        finish();
                                    }else {
                                        Toast.makeText(context, "分享失败！请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(context, "分享失败！请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*op.insert("insert into Share(UserID,Title,Content,Date,Count) values(?,?,?,?,0)",new String[]{sp.getID(),"健身心得与知识",content.getText().toString(), time});
                //判断是否分享成功
                List<Map<String, String>> data = new ArrayList<>();
                data = op.select("select count(*) count from Share where UserID=? and Date=? ",
                        new String[]{sp.getID(), time});
                if (data.get(0).get("count").equals("1")) {
                    Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.InteractSy$MyBroad")));
                    finish();
                } else {
                    Toast.makeText(context, "分享失败！请稍后重试", Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        send.setEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(content.getText().length()!=0){
            send.setEnabled(true);
        }
    }
}
