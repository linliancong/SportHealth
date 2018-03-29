package com.develop.tools.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.develop.bean.StepEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/16.
 * 数据库操作工具
 */
public class SQLOperator {
    //声明数据库管理器
    private SQLOpenHelper openHelper;

    //实例化数据库管理器
    public SQLOperator(Context context) {
        openHelper = new SQLOpenHelper(context);
    }

    /**
     * 查询数据库
     * @param sql 查询语句
     * @param condition 条件
     * @return
     * */
    public List<Map<String,String>> select(String sql, String[] condition)
    {
        //获得可读数据库句柄,通常内部实现返回的其实都是可写的数据库句柄
        SQLiteDatabase db = openHelper.getReadableDatabase();
        //查询数据,返回的Cursor指向第一条记录之前
        Cursor cursor = db.rawQuery(sql,condition);
        //所有数据集合
        List<Map<String,String>> datas=new ArrayList<>();

        //开始遍历数据
        while(cursor.moveToNext())
        {
            //一条数据的集合
            Map<String, String> data = new HashMap<>();
            for (int i=0;i<cursor.getColumnCount();i++){
                String coulumnName=cursor.getColumnName(i);
                String value=cursor.getString(cursor.getColumnIndex(coulumnName));
                data.put(coulumnName,value);
            }
            datas.add(data);
        }
        cursor.close();//关闭cursor,释放资源;
        db.close();
        return datas;
    }

    /**
     * 数据操作
     * @param sql 操作语句
     * @param condition 条件
     */
    public void insert(String sql, String[] condition)
    {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();
        try{
            db.execSQL(sql, condition);
            //成功：提交事务,失败：事务回滚
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally{
            //结束事务
            db.endTransaction();
        }
        db.close();
    }


    /**
     * 以下三个方法均为保存步数的方法
    * */

    /**
     * 添加一条新记录
     *
     * @param stepEntity
     */
    public void addNewData(StepEntity stepEntity) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Date", stepEntity.getCurDate());
        values.put("TotalSteps", stepEntity.getSteps());
        values.put("UserID", stepEntity.getUserID());
        db.insert("Step", null, values);

        db.close();
    }

    /**
     * 根据日期查询记录
     *
     * @param curDate
     * @return
     */
    public StepEntity getCurDataByDate(String curDate,String userID) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        StepEntity stepEntity = null;
        String str="select * from Step where Date=? and UserID=?";
        Cursor cursor = db.rawQuery(str,new String[]{curDate,userID});
        //Cursor cursor = db.query("Step", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));
            if (curDate.equals(date)) {
                String steps = cursor.getString(cursor.getColumnIndexOrThrow("TotalSteps"));
                stepEntity = new StepEntity(date, steps);
                //跳出循环
                break;
            }
        }
        //关闭
        db.close();
        cursor.close();
        return stepEntity;
    }

    /**
     * 更新数据
     * @param stepEntity
     */
    public void updateCurData(StepEntity stepEntity) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Date",stepEntity.getCurDate());
        values.put("TotalSteps", stepEntity.getSteps());
        values.put("UserID", stepEntity.getUserID());
        db.update("Step", values, "Date=? and UserID=?", new String[]{stepEntity.getCurDate(),stepEntity.getUserID()});

        db.close();
    }

}
