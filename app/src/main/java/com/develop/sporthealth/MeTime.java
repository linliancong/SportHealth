package com.develop.sporthealth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVObject;
import com.bigkoo.pickerview.TimePickerView;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/19.
 */

public class MeTime extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private MyLayout back;
    private Button save;
    private EditText edit;
    private Calendar selectedDate;
    private Context context;
    private SPTools sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_time);
        back=findViewById(R.id.time_myly_update);
        save=findViewById(R.id.time_btn_update);
        edit=findViewById(R.id.time_txt_update);
        selectedDate=Calendar.getInstance();
        context=getApplicationContext();
        sp=new SPTools(context);
        save.setOnClickListener(this);
        edit.setOnClickListener(this);
        edit.addTextChangedListener(this);
        edit.setText(sp.getDate());


        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void TimePicker(){
        TimePickerView time=new TimePickerView.Builder(MeTime.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                    edit.setText(getTime(date));

            }
        })
                .setDate(selectedDate)
                .setType(new boolean[]{false,false,false,true,true,true})
                .setLabel("","","","","","")
                .build();
        //精确到秒的时间显示
        //time.setDate(Calendar.getInstance());
        time.show();
    }

    //可根据需要自行截取数据显示
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_btn_update:
                AVObject testObject1 = AVObject.createWithoutData("UserInfo", sp.getID());
                testObject1.put("SendTime", edit.getText().toString());
                // 保存到云端
                testObject1.saveInBackground();
                sp.setDate(edit.getText().toString());
                finish();
                break;
            case R.id.time_txt_update:
                InputMethodManager input= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (input.isActive()) {
                    input.hideSoftInputFromWindow( v.getApplicationWindowToken() , 0 );
                }
                TimePicker();
                break;
        }
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
        if(edit.getText().length()>0){
            save.setEnabled(true);
        }

    }
}
