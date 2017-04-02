package com.example.younsuk.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.younsuk.criminalintent.database.CrimeBaseHelper;
import com.example.younsuk.criminalintent.database.CrimeCursorWrapper;
import com.example.younsuk.criminalintent.database.CrimeDbSchema;
import com.example.younsuk.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Younsuk on 8/28/2015.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    //----------------------------------------------------------------------------------------------
    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }
    //----------------------------------------------------------------------------------------------
    public static CrimeLab get(Context context){
        if(sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }
    //----------------------------------------------------------------------------------------------
    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return crimes;
    }
    //----------------------------------------------------------------------------------------------
    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Columns.UUID + " = ?", new String[]{ id.toString() });
        try {
            if(cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }
    }
    //----------------------------------------------------------------------------------------------
    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Columns.UUID + " = ?", new String[]{ uuidString });
    }
    //----------------------------------------------------------------------------------------------
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Columns.UUID, crime.getId().toString());
        values.put(CrimeTable.Columns.TITLE, crime.getTitle());
        values.put(CrimeTable.Columns.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Columns.TIME, crime.getTimes().getTime());
        values.put(CrimeTable.Columns.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Columns.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Columns.PHONE_NUMBER, crime.getPhoneNumber());
        return values;
    }
    //----------------------------------------------------------------------------------------------
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
    //----------------------------------------------------------------------------------------------
    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null)
            return null;
        return new File(externalFilesDir, crime.getPhotoFileName());
    }
    //----------------------------------------------------------------------------------------------
    public void addCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }
    //----------------------------------------------------------------------------------------------
    public void deleteCrime(Crime crime){
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Columns.UUID + " = ?", new String[]{crime.getId().toString()});
    }
    //----------------------------------------------------------------------------------------------
    public void deleteCrimes(List<Crime> deletionTargetList){
        for(Crime crime: deletionTargetList)
            deleteCrime(crime);
    }
    //----------------------------------------------------------------------------------------------
}
