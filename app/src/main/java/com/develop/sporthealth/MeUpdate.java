package com.develop.sporthealth;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.avos.avoscloud.FindCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/7.
 */

public class MeUpdate extends AppCompatActivity implements TextWatcher,View.OnClickListener {

    private MyLayout update;
    private Button save;
    private EditText input;

    private String content;
    private String value;

    private Intent it;
    private SPTools sp;
    private SQLOperator op;
    private Context context;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("com.develop.sport.MYBROAD").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.MeSy$MyBroad")));
                    break;
                case 0x002:
                    Toast.makeText(context,"修改失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_info);
        //AppManager.getAppManager().addActivity(MeSyUpdate.this);
        init();


    }


    public void init() {
        context=MeUpdate.this;
        sp=new SPTools(context);
        op=new SQLOperator(context);
        update = findViewById(R.id.me_myly_update);
        save = findViewById(R.id.me_btn_update);
        input = findViewById(R.id.me_txt_update);

        it = getIntent();
        Bundle bd = it.getExtras();
        content = bd.getString("STR","");
        value = bd.getString("VALUE","");
        input.setText(content);
        input.setSelection(content.length());

        input.addTextChangedListener(this);
        save.setOnClickListener(this);
        update.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (value) {
            case "Name":
                update.setText("姓名");
                break;
            case "Sex":
                update.setText("性别");
                break;
            case "Weight":
                update.setText("体重");
                break;
            case "UserName":
                update.setText("账号");
                break;
            case "Email":
                update.setText("邮箱");
                break;
            case "Phone":
                update.setText("手机");
                break;
            case "QQ":
                update.setText("QQ");
                break;
            default:
                break;

        }
    }

    @Override
    public void onClick(View v) {
        update(value);
        finish();
    }

    private void update(String sql) {

        // 第一参数是 className,第二个参数是 objectId
        AVObject testObject1 = AVObject.createWithoutData("UserInfo", sp.getID());

        testObject1.put(sql,input.getText().toString());
        // 保存到云端
        testObject1.saveInBackground();
        //查询是否更新成功
        AVQuery<AVObject> query1 = new AVQuery<>("UserInfo");
        query1.whereEqualTo(sql,input.getText().toString());
        AVQuery<AVObject> query2 = new AVQuery<>("UserInfo");
        query2.whereEqualTo("UserName",sp.getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0){
                    handler.sendEmptyMessage(0x001);
                }else {
                    handler.sendEmptyMessage(0x002);
                }
            }
        });

       /* List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        op.insert("update UserInfo set "+sql+"=? where id=?", new String[]{input.getText().toString(),sp.getID()});
        //判断是否修改成功
        data = op.select("select count(1) num from UserInfo where "+sql+"=? and id=?", new String[]{input.getText().toString(),sp.getID()});
        if (data.size() != 0) {
            map = data.get(0);
            if (map.get("num").toString().equals("1")) {
                handler.sendEmptyMessage(0x001);
            }else {
                handler.sendEmptyMessage(0x002);
            }
        }*/
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        save.setEnabled(false);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (input.getText().toString().length() != 0) {
            save.setEnabled(true);
        }

    }

}
