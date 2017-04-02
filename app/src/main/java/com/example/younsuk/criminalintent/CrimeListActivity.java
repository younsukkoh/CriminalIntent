package com.example.younsuk.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Younsuk on 8/28/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

}
