package com.example.younsuk.criminalintent;

import android.content.ContentValues;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Younsuk on 8/27/2015.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Date mTime;
    private boolean mSolved;
    private String mSuspect;
    private String mPhoneNumber;
    //----------------------------------------------------------------------------------------------
    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
        mTime = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() { return mSolved; }

    public void setSolved(boolean solved) { mSolved = solved; }

    public Date getTimes() { return mTime; }

    public void setTime(Date time) { mTime = time; }

    public String getSuspect() { return mSuspect; }

    public void setSuspect(String suspect) { mSuspect = suspect; }

    public String getPhoneNumber() { return mPhoneNumber; }

    public void setPhoneNumber(String phoneNumber) { mPhoneNumber = phoneNumber; }

    public String getPhotoFileName(){ return "IMG_" + getId().toString() + ".jpd"; }
}
