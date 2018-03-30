package com.develop.tools;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具类
 */

public class TimeTools {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy年MM月");
    private static SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd");
    private static Calendar mCalendar = Calendar.getInstance();
    private static String[] weekStrings = new String[]{"日","一", "二", "三", "四", "五", "六"};
    private static String[] rWeekStrings = new String[]{"周日","周一", "周二", "周三", "周四", "周五", "周六"};


    /**
     * 改变日期格式
     * @param date  2017年02月09日
     * @return 2017-02-09
     */
    public static String changeFormatDate(String date){
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = null;
        try {
            Date dt = dateFormat.parse(date);
            curDate = dFormat.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curDate;
    }

    /**
     * 返回当前的时间
     * @return  今天 09:48
     */
    public static String getCurTime(){
        SimpleDateFormat dFormat = new SimpleDateFormat("HH:mm");
        String time = "今天 "+dFormat.format(System.currentTimeMillis());
        return time;
    }

    /**
     * 获取运动记录是周几，今天则返回具体时间，其他则返回具体周几
     * @param dateStr
     * @return
     */
    public static String getWeekStr(String dateStr){
        mCalendar = Calendar.getInstance();

        String todayStr = dateFormat.format(mCalendar.getTime());

        if(todayStr.equals(dateStr)){
            return getCurTime();
        }

        Calendar preCalendar = Calendar.getInstance();
        preCalendar.add(Calendar.DATE, -1);
        String yesterdayStr = dateFormat.format(preCalendar.getTime());
        if(yesterdayStr.equals(dateStr)){
            return "昨天";
        }

        int w = 0;
        try {
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rWeekStrings[w];
    }


    /**
     * 获取是几号
     *
     * @return dd
     */
    public static int getCurrentDay() {
        return mCalendar.get(Calendar.DATE);
    }

    /**
     * 获取当前的日期
     *
     * @return yyyy年MM月dd日
     */
    public static String getCurrentDate() {
        mCalendar = Calendar.getInstance();
        String currentDateStr = dateFormat.format(mCalendar.getTime());
        return currentDateStr;
    }

    /**
     * 获取当前的日期
     *
     * @return yyyy/MM/dd/ HH:mm:ss
     */
    public static String getCurrentDate2() {
        mCalendar = Calendar.getInstance();
        String currentDateStr = dateFormat2.format(mCalendar.getTime());
        return currentDateStr;
    }

    /**
     * 获取当前的日期
     *
     * @return yyyy年MM月
     */
    public static String getCurrentDate3() {
        mCalendar = Calendar.getInstance();
        String currentDateStr = dateFormat3.format(mCalendar.getTime());
        return currentDateStr;
    }


    /**
     * 根据date列表获取day列表
     *
     * @param dateList
     * @return
     */
    public static List<Integer> dateListToDayList(List<String> dateList) {
        Calendar calendar = Calendar.getInstance();
        List<Integer> dayList = new ArrayList<>();
        for (String date : dateList) {
            try {
                calendar.setTime(dateFormat.parse(date));
                int day = calendar.get(Calendar.DATE);
                dayList.add(day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dayList;
    }


    /**
     * 根据当前日期获取以含当天的前一周日期
     * @return [2017年02月21日, 2017年02月22日, 2017年02月23日, 2017年02月24日, 2017年02月25日, 2017年02月26日, 2017年02月27日]
     */
    public static List<String> getBeforeDateListByNow(){
        List<String> weekList = new ArrayList<>();

        for (int i = -6; i <= 0; i++) {
            //以周日为一周的第一天
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            String date = dateFormat.format(calendar.getTime());
            weekList.add(date);
        }
        return weekList;
    }

    /**
     * 判断当前日期是周几
     * @param curDate
     * @return
     */
    public static String getCurWeekDay(String curDate){
        int w = 0;
        try {
            Date date = dateFormat.parse(curDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        //以周日为一周的第一天

        return weekStrings[w];
    }


    /**
     * 获取本周一的日期
     * @param
     * @return
     */
    public static String getCurWeekMon(){
        int w = 0;
        String redate="";
        try {
            String curDate=dateFormat.format(new Date());
            Date date = dateFormat.parse(curDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }
            calendar.add(Calendar.DATE, w-2*w+1);
            redate = dateFormat.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return redate;
    }

    /**
     * 获取本周一的日期
     * @param
     * @return
     * 返回：yyyy-MM-dd
     */
    public static String getCurWeek(){
        int w = 0;
        String redate="";
        try {
            String curDate=dateFormat4.format(new Date());
            Date date = dateFormat4.parse(curDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }
            calendar.add(Calendar.DATE, w-2*w+1);
            redate = dateFormat4.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return redate;
    }

    /**
     * 获取本月第一天
     * @param
     * @return
     * 返回：yyyy-MM-dd
     */
    public static String getCurMonth(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        return dateFormat4.format(cal.getTime());
    }

    /**
     * 获取本年第一天
     * @param
     * @return
     * 返回：yyyy-MM-dd
     */
    public static String getCurYear(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.getTime();
        return dateFormat4.format(cal.getTime());
    }
}
