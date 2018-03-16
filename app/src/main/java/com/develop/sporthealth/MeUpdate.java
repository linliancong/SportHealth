package com.develop.sporthealth;


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

import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/7.
 */

public class MeUpdate extends AppCompatActivity implements TextWatcher,View.OnClickListener {

    private MyLayout person_imgtxt_update;
    private Button person_btn_update;
    private EditText person_txt_update;

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
                    sendBroadcast(new Intent("com.develop.sport.MYBROAD"));
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
        person_imgtxt_update = findViewById(R.id.person_imgtxt_update);
        person_btn_update = findViewById(R.id.person_btn_update);
        person_txt_update = findViewById(R.id.person_txt_update);

        it = getIntent();
        Bundle bd = it.getExtras();
        content = bd.getString("STR","");
        value = bd.getString("VALUE","");
        person_txt_update.setText(content);
        person_txt_update.setSelection(content.length());

        person_txt_update.addTextChangedListener(this);
        person_btn_update.setOnClickListener(this);
        person_imgtxt_update.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (value) {
            case "Name":
                person_imgtxt_update.setText("姓名");
                break;
            case "Sex":
                person_imgtxt_update.setText("性别");
                break;
            case "UserName":
                person_imgtxt_update.setText("账号");
                break;
            case "Email":
                person_imgtxt_update.setText("邮箱");
                break;
            case "Phone":
                person_imgtxt_update.setText("手机");
                break;
            case "QQ":
                person_imgtxt_update.setText("QQ");
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
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        op.insert("update UserInfo set "+sql+"=? where id=?", new String[]{person_txt_update.getText().toString(),sp.getID()});
        //判断是否修改成功
        data = op.select("select count(1) num from UserInfo where "+sql+"=? and id=?", new String[]{person_txt_update.getText().toString(),sp.getID()});
        if (data.size() != 0) {
            map = data.get(0);
            if (map.get("num").toString().equals("1")) {
                handler.sendEmptyMessage(0x001);
            }else {
                handler.sendEmptyMessage(0x002);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        person_btn_update.setEnabled(false);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (person_txt_update.getText().toString().length() != 0) {
            person_btn_update.setEnabled(true);
        }

    }

}
