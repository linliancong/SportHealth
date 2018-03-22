package com.develop.bean;

import android.widget.ThemedSpinnerAdapter;

/**
 * Created by Administrator on 2018/3/21.
 */

public class Plans {
    private String title;
    private String plan;
    private String summary;
    private String date;

    public Plans(){}

    public Plans(String title,String plan,String summary,String date){
        this.title=title;
        this.plan=plan;
        this.summary=summary;
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public String getPlan() {
        return plan;
    }

    public String getSummary() {
        return summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
