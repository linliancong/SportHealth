package com.develop.tools.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/3/16
 * 数据库表工具
 */
public class SQLOpenHelper extends SQLiteOpenHelper {

    public SQLOpenHelper(Context context){
        super(context,"Sport.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 表名：UserInfo （用户信息表）
         * 以下为各个字段
         * id：用户编号
         * UserName：用户名
         * Password：密码
         * Name：姓名
         * Sex：性别
         * Phone：手机
         * Email：邮箱
         * QQ：qq号
         * Image：头像
         * Weight：体重
         * Remark：备注
        * */
        db.execSQL("create table if not exists UserInfo"+
                "(id integer primary key autoincrement,"+
                "UserName varchar(50),"+
                "Password varchar(50),"+
                "Name varchar(50),"+
                "Sex varchar(50),"+
                "Phone varchar(50),"+
                "Email varchar(50),"+
                "QQ varchar(50),"+
                "Image varchar(50),"+
                "Weight varchar(50),"+
                "Remark varchar(50))");

        /**
         * 表名：SportInfo（运动信息表）
         * 以下为各个字段
         * id：运动编号
         * Name：运动名称
         * Summary：运动简介
         * Remark：备注
         * */
        db.execSQL("create table if not exists SportInfo"+
                "(id integer primary key autoincrement,"+
                "Name varchar(50),"+
                "Summary varchar(500),"+
                "Remark varchar(100))");

        /**
         * 表名：SportPlan（运动计划表）
         * 以下为各个字段
         * id：计划编号
         * UserID：用户编号
         * SportID：运动编号
         * State：计划状态（1、进行中，2、已完成）
         * Target：计划目标
         * StartDate：计划创建时间
         * FinalDate：计划完成时间
         * Remark：备注
         * */
        db.execSQL("create table if not exists SportPlan"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "SportID integer,"+
                "State integer,"+
                "Target integer,"+
                "StartDate varchar(50),"+
                "FinalDate varchar(50),"+
                "Remark varchar(50))");

        /**
         * 表名：SportLocation（运动轨迹表）
         * 以下为各个字段
         * id：id
         * UserID：用户编号
         * RunID：统计编号
         * Longitude：经度
         * Latitude：纬度
         * Speed：配速
         * Time：时间
         * Remark：备注
         * */
        db.execSQL("create table if not exists SportLocation"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "RunID integer,"+
                "Longitude varchar(50),"+
                "Latitude varchar(50),"+
                "Speed varchar(50),"+
                "Time varchar(50),"+
                "Remark varchar(50))");

        /**
         * 表名：SportRunning（跑步统计表）
         * 以下为各个字段
         * id：统计编号
         * UserID：用户编号
         * SportID：运动编号
         * Total：运动公里数
         * Time：运动时间
         * Speed：配速
         * Hot：热量
         * StartTime：开始时间
         * EndTime：结束时间
         * Remark：备注
         * */
        db.execSQL("create table if not exists SportRunning"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "SportID integer,"+
                "Total integer,"+
                "Time integer,"+
                "Speed integer,"+
                "Hot integer,"+
                "StartTime varchar(50),"+
                "EndTime varchar(50),"+
                "Remark varchar(50))");

        /**
         * 表名：SportFinish（运动完成情况表）
         * 以下为各个字段
         * id：id
         * UserID：用户编号
         * SportID：运动编号
         * Time：时间
         * Remark：备注
         * */
        db.execSQL("create table if not exists SportFinish"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "SportID integer,"+
                "Time varchar(50),"+
                "Remark varchar(50))");


        /**
         * 表名：Messages（消息表）
         * 以下为各个字段
         * id：消息编号
         * UserID：用户编号
         * Title：标题
         * Content：内容
         * Date：时间
         * State：状态（1：未读，2：已读）
         * */
        db.execSQL("create table if not exists Messages"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "Title varchar(50),"+
                "Content varchar(100),"+
                "Date varchar(50),"+
                "State integer)");

        /**
         * 表名：Step（步数表）
         * 以下为各个字段
         * id：
         * CurDate：时间
         * TotalSteps：步数
         * */
        db.execSQL("create table Step (" +
                "id integer primary key autoincrement, " +
                "CurDate TEXT, " +
                "TotalSteps TEXT)");

        /**
         * 表名：Share（分享信息表）
         * 以下为各个字段
         * id：
         * UserID：用户编号
         * Title：标题
         * Content：内容
         * Date：时间
         * Count：点赞个数
         * */
        db.execSQL("create table if not exists Share"+
                "(id integer primary key autoincrement,"+
                "UserID integer,"+
                "Title varchar(50),"+
                "Content varchar(500),"+
                "Date varchar(50),"+
                "Count integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本号变化时操作
        db.execSQL("drop table UserInfo");
        db.execSQL("drop table SportInfo");
        db.execSQL("drop table SportPlan");
        db.execSQL("drop table SportLocation");
        db.execSQL("drop table Messages");
        onCreate(db);
    }
}
