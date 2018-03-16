package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/3/16.
 */

public class InteractSy extends Fragment {

    private Context context;
    private View view;

    public InteractSy(){}
    @SuppressLint("ValidFragment")
    public InteractSy(Context context){
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.interact, container, false);
        }
        return view;
    }
}
