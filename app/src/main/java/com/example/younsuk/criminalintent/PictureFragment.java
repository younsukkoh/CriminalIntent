package com.example.younsuk.criminalintent;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Younsuk on 9/6/2015.
 */
public class PictureFragment extends DialogFragment {
    private static final String ARG_EVIDENCE = "arg_picture";
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_picture, parent, false);
        getDialog().setTitle("Evidence of the Crime:");

        ImageView evidenceView = (ImageView)view.findViewById(R.id.evidence_container);

        File evidenceFile = (File)getArguments().getSerializable(ARG_EVIDENCE);
        Bitmap bitmap = PictureUtils.getScaledBitMap(evidenceFile.getPath(), getActivity());

        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(evidenceFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotate = 270;
            else if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotate = 180;
            else if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotate = 90;
        }
        catch (IOException e){ }

        evidenceView.setRotation(rotate);

        evidenceView.setImageBitmap(bitmap);

        return view;
    }
    //----------------------------------------------------------------------------------------------
    public static PictureFragment newInstance(File file){
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVIDENCE, file);

        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //----------------------------------------------------------------------------------------------
}
