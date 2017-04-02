package com.example.younsuk.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.younsuk.criminalintent.Crime;
import com.example.younsuk.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Younsuk on 9/2/2015.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    //----------------------------------------------------------------------------------------------
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    //----------------------------------------------------------------------------------------------
    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Columns.UUID));
        String title = getString(getColumnIndex(CrimeTable.Columns.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Columns.DATE));
        long time = getLong(getColumnIndex(CrimeTable.Columns.TIME));
        int isSolved = getInt(getColumnIndex(CrimeTable.Columns.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Columns.SUSPECT));
        String phoneNumber = getString(getColumnIndex(CrimeTable.Columns.PHONE_NUMBER));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setTime(new Date(time));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setPhoneNumber(phoneNumber);

        return crime;
    }
    //----------------------------------------------------------------------------------------------
}
