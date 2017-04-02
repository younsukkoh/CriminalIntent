package com.example.younsuk.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * Created by Younsuk on 8/27/2015.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "arg_crime_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final String DIALOG_TIME = "dialog_time";
    private static final String DIALOG_PICTURE = "dialog_picture";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_SUSPECT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCrime = new Crime();
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { mCrime.setTitle(s.toString()); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mDateButton = (Button)view.findViewById(R.id.crime_date);
        updateDateTime();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();

                DatePickerFragment dateDialog = DatePickerFragment.newInstance(mCrime.getDate());
                dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dateDialog.show(fm, DIALOG_DATE);

                TimePickerFragment timeDialog = TimePickerFragment.newInstance(mCrime.getDate());
                timeDialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                timeDialog.show(fm, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox)view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button)view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_SUSPECT);
            }
        });
        if(mCrime.getSuspect() != null)
            mSuspectButton.setText("Suspect: " + mCrime.getSuspect());

        mCallButton = (Button)view.findViewById(R.id.crime_suspect_call);
        if(mCrime.getPhoneNumber() == null)
            mCallButton.setEnabled(false);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + mCrime.getPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        mPhotoButton = (ImageButton)view.findViewById(R.id.crime_camera);
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(mPhotoFile != null){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhotoFile != null)
                    CrimeLab.get(getActivity()).getPhotoFile(mCrime).delete();
                startActivityForResult(captureImageIntent, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)view.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhotoFile == null || !mPhotoFile.exists()){
                    Toast.makeText(getActivity(), "There is No Picture Evidence", Toast.LENGTH_SHORT).show();
                }
                else{
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    PictureFragment pictureDialog = PictureFragment.newInstance(mPhotoFile);
                    pictureDialog.show(fm, DIALOG_PICTURE);
                }
            }
        });

        try {
            updatePhotoView();
        }
        catch (IOException e){}

        return view;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.fragment_crime_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                Toast.makeText(getActivity(), "Crime has been removed.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //----------------------------------------------------------------------------------------------
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == REQUEST_DATE){
            Date date = (Date)intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDateTime();
        }

        else if(requestCode == REQUEST_TIME){
            Date time = (Date)intent.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateDateTime();
        }

        else if(requestCode == REQUEST_SUSPECT && intent != null) {
            Uri contactUri = intent.getData();
            Cursor nameCursor = getActivity().getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
            try {
                if (nameCursor.getCount() == 0)
                    return;
                nameCursor.moveToFirst();
                String suspect = nameCursor.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText("Suspect: " + suspect);
                mCallButton.setEnabled(true);
            } finally {
                nameCursor.close();
            }

            Cursor idCursor = getActivity().getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts._ID}, null, null, null);
            String chosenContactId;
            try {
                if(idCursor.getCount() == 0)
                    return;
                idCursor.moveToFirst();
                chosenContactId = idCursor.getString(0);
            }
            finally {
                idCursor.close();
            }

            Cursor numberCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ ContactsContract.CommonDataKinds.Phone.NUMBER }, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + chosenContactId, null, null);
            try {
                if(numberCursor.getCount() == 0)
                    return;
                numberCursor.moveToFirst();
                mCrime.setPhoneNumber(numberCursor.getString(0));
            }
            finally {
                numberCursor.close();
            }
        }

        else if (requestCode == REQUEST_PHOTO){
            try {
                updatePhotoView();
            }
            catch (IOException e){}
        }
    }
    //----------------------------------------------------------------------------------------------
    private void updateDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("EEEE MMM dd, yyyy");
        String dateFormatted = dateFormat.format(mCrime.getDate());
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeFormatted = timeFormat.format(mCrime.getTimes());
        mDateButton.setText(dateFormatted + " || " + timeFormatted);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
    //----------------------------------------------------------------------------------------------
    private String getCrimeReport(){

        String crimeTitle = mCrime.getTitle();
        if(crimeTitle == null)
            crimeTitle = "No title for this crime";

        String solvedString;
        if(mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateString = new SimpleDateFormat("EEE MMM dd").format(mCrime.getDate());

        String timeString = new SimpleDateFormat("hh:mm a").format(mCrime.getTimes());

        String suspect = mCrime.getSuspect();
        if(suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect, suspect);

        String report = getString(R.string.crime_report, crimeTitle, dateString + ", " + timeString, solvedString, suspect);

        return report;
    }
    //----------------------------------------------------------------------------------------------
    private void updatePhotoView() throws IOException{
        if(mPhotoFile == null || !mPhotoFile.exists())
            mPhotoView.setImageDrawable(null);
        else{
            Bitmap bitmap = PictureUtils.getScaledBitMap(mPhotoFile.getPath(), getActivity());

            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(mPhotoFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                    rotate = 270;
                else if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                    rotate = 180;
                else if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                    rotate = 90;
            }
            catch (IOException e){

            }
            mPhotoView.setRotation(rotate);

            mPhotoView.setImageBitmap(bitmap);
        }
    }
    //----------------------------------------------------------------------------------------------
}
