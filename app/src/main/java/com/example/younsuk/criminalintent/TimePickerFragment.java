package com.example.younsuk.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Younsuk on 8/31/2015.
 */
public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "arg_time";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    private TimePicker mTimePicker;
    //----------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Date date = (Date)getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker)view.findViewById(R.id.dialog_time_timePicker);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date time = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(time);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                            c.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                            c.set(Calendar.MINUTE, mTimePicker.getMinute());
                        } else {
                            c.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                            c.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                        }
                        time = c.getTime();

                        sendResult(Activity.RESULT_OK, time);
                    }
                })
                .create();

    }
    //----------------------------------------------------------------------------------------------
    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //----------------------------------------------------------------------------------------------
    private void sendResult(int resultCode, Date time){
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
    //----------------------------------------------------------------------------------------------
}
