package com.example.educher_parent;

public class ScheduleModel {
    private String from,to,day,key,parentkey,childiD;

    public ScheduleModel(String from, String to, String day, String key, String parentkey, String childiD) {
        this.from = from;
        this.to = to;
        this.day = day;
        this.key = key;
        this.parentkey = parentkey;
        this.childiD = childiD;
    }

    public String getParentkey() {
        return parentkey;
    }

    public void setParentkey(String parentkey) {
        this.parentkey = parentkey;
    }

    public String getChildiD() {
        return childiD;
    }

    public void setChildiD(String childiD) {
        this.childiD = childiD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
