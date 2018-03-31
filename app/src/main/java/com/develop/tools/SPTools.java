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


    public float getWeight()
    {
        return sp.getFloat("Weight",0);
    }

    //用户体重
    public void setWeight(float Weight){
        editor.putFloat("Weight",Weight);
        editor.commit();
    }

    //用户头像URL
    public void setImage(String Image){
        editor.putString("Image",Image);
        editor.commit();
    }

    public String getImage()
    {
        return sp.getString("Image","");
    }

    //用户推送的ID
    public void setInstallationId(String InstallationId){
        editor.putString("InstallationId",InstallationId);
        editor.commit();
    }

    public String getInstallationId()
    {
        return sp.getString("InstallationId","");
    }

    //用户推送的时间
    public void setDate(String Date){
        editor.putString("Date",Date);
        editor.commit();
    }

    public String getDate()
    {
        return sp.getString("Date","");
    }

    //今天是否推送
    public void setIsSend(boolean IsSend){
        editor.putBoolean("IsSend",IsSend);
        editor.commit();
    }

    public boolean getIsSend()
    {
        return sp.getBoolean("IsSend",false);
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
