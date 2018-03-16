package com.develop.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/3/16.
 * 常用信息工具类
 */

public class SPTools {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SPTools(Context context){
        sp=context.getSharedPreferences("SportSP",context.MODE_PRIVATE);
        editor=sp.edit();
    }


    //用户ID
    public void setID(String ID){
        editor.putString("ID",ID);
        editor.commit();
    }

    public String getID()
    {
        return sp.getString("ID","");
    }

    //用户账户
    public void setUserName(String UserName){
        editor.putString("UserName",UserName);
        editor.commit();
    }

    public String getUserName()
    {
        return sp.getString("UserName","");
    }

    //用户名
    public void setName(String Name){
        editor.putString("Name",Name);
        editor.commit();
    }

    public String getName()
    {
        return sp.getString("Name","");
    }

    //用户密码
    public void setPWD(String pwd){
        editor.putString("PWD",pwd);
        editor.commit();
    }

    public String getPWD()
    {
        return sp.getString("PWD","");
    }

    // 是否第一次运行
    public void setIsFirst(boolean isFirst) {
        editor.putBoolean("isFirst", isFirst);
        editor.commit();
    }

    public boolean getIsFirst() {
        return sp.getBoolean("isFirst", true);
    }

    // 是否登录
    public void setIsLogin(boolean isLogin) {
        editor.putBoolean("isLogin", isLogin);
        editor.commit();
    }

    public boolean getIsLogin() {
        return sp.getBoolean("isLogin", false);
    }


    // 是否更新用户资料
    public void setIsUpdate(boolean isUpdate) {
        editor.putBoolean("isUpdate", isUpdate);
        editor.commit();
    }

    public boolean getIsUpdate() {
        return sp.getBoolean("isUpdate", false);
    }



}
