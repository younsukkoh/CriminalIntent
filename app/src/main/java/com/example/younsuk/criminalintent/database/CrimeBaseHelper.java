package com.example.younsuk.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.younsuk.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by Younsuk on 9/2/2015.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";
    //----------------------------------------------------------------------------------------------
    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL("create table " + CrimeTable.NAME + "(" + "_id integer primary key autoincrement, " +
                        CrimeTable.Columns.UUID + ", " +
                        CrimeTable.Columns.TITLE + ", " +
                        CrimeTable.Columns.DATE + ", " +
                        CrimeTable.Columns.TIME + ", " +
                        CrimeTable.Columns.SOLVED + ", " +
                        CrimeTable.Columns.SUSPECT + ", " +
                        CrimeTable.Columns.PHONE_NUMBER +
                        ")"
        );
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

    }
    //----------------------------------------------------------------------------------------------
}