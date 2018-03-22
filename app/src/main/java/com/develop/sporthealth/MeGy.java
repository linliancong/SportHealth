package com.develop.sporthealth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.develop.tools.MyLayout;

/**
 * Created by Administrator on 2018/3/19.
 */

public class MeGy extends AppCompatActivity {

    private MyLayout back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_gy);
        back=findViewById(R.id.me_back);

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
