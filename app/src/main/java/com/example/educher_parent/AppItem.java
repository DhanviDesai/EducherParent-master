package com.example.educher_parent;

public class AppItem {
    public String mName;
    public String mPackageName;
    public String mEventTime;
    public String mUsageTime;
    public String mEventType;
    public String mCount;
    public String mMobile;
    public String mCanOpen;

    public AppItem(String mName, String mPackageName, String mEventTime, String mUsageTime, String mEventType, String mCount, String mMobile, String mCanOpen) {
        this.mName = mName;
        this.mPackageName = mPackageName;
        this.mEventTime = mEventTime;
        this.mUsageTime = mUsageTime;
        this.mEventType = mEventType;
        this.mCount = mCount;
        this.mMobile = mMobile;
        this.mCanOpen = mCanOpen;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getmEventTime() {
        return mEventTime;
    }

    public void setmEventTime(String mEventTime) {
        this.mEventTime = mEventTime;
    }

    public String getmUsageTime() {
        return mUsageTime;
    }

    public void setmUsageTime(String mUsageTime) {
        this.mUsageTime = mUsageTime;
    }

    public String getmEventType() {
        return mEventType;
    }

    public void setmEventType(String mEventType) {
        this.mEventType = mEventType;
    }

    public String getmCount() {
        return mCount;
    }

    public void setmCount(String mCount) {
        this.mCount = mCount;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmCanOpen() {
        return mCanOpen;
    }

    public void setmCanOpen(String mCanOpen) {
        this.mCanOpen = mCanOpen;
    }
}
