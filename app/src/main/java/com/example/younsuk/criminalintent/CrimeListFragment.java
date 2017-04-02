package com.example.younsuk.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Younsuk on 8/28/2015.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private TextView mEmptyView;
    private CrimeAdapter mAdapter;
    private int updateTarget;
    private boolean mSubtitleVisible;
    private ActionMode mActionMode;
    private HashMap viewList;
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        mEmptyView = (TextView)view.findViewById(R.id.empty_view);
        checkEmptyView();

        return view;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible)
            subtitleItem.setTitle(R.string.hide_subtitle);
        else
            subtitleItem.setTitle(R.string.show_subtitle);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //----------------------------------------------------------------------------------------------
    private void updateUI(){
        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }
    //----------------------------------------------------------------------------------------------
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_crime_title_textView);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_crime_date_textView);
            mSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_crime_solved_checkBox);
        }

        public void bindCrime(Crime crime){
            mCrime = crime;
            String crimeTitle = mCrime.getTitle();
            if(crimeTitle == null)
                mTitleTextView.setText("NO TITLE FOR THE CRIME!");
            else
                mTitleTextView.setText(crimeTitle);

            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd, yyyy");
            String dateFormatted = dateFormat.format(mCrime.getDate());
            DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            String timeFormatted = timeFormat.format(mCrime.getTimes());
            mDateTextView.setText(dateFormatted + " || " + timeFormatted);

            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View view){
            if(mActionMode == null){
                updateTarget = getAdapterPosition(); //Challenge Chapter 10
                Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                startActivity(intent);
            }
            else{
                boolean isSelected = mAdapter.toggleSelected(getAdapterPosition());
                view.setActivated(isSelected);

                if(isSelected)
                    viewList.put(getAdapterPosition(), view);
                else
                    viewList.remove(getAdapterPosition());

                int selectedNum = mAdapter.getSelectedItemCount();
                if(selectedNum == 0)
                    mActionMode.setTitle(R.string.crimes_selected_context_zero);
                else{
                    String contextTitle = getResources().getQuantityString(R.plurals.crimes_selected_context, selectedNum, selectedNum);
                    mActionMode.setTitle(contextTitle);
                }
            }
        }

        @Override
        public boolean onLongClick(View view){
            if(mActionMode == null) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);

                boolean isSelected = mAdapter.toggleSelected(getAdapterPosition());
                view.setActivated(isSelected);

                viewList.put(getAdapterPosition(), view);

                int selectedNum = mAdapter.getSelectedItemCount();
                String contextTitle = getResources().getQuantityString(R.plurals.crimes_selected_context, selectedNum, selectedNum);
                mActionMode.setTitle(contextTitle);
            }
            return true;
        }

        private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.fragment_crime_list_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_delete_crime:
                        int crimesToBeDeletedNum = mAdapter.getSelectedItemCount();
                        if(crimesToBeDeletedNum == 1)
                            Toast.makeText(getActivity(), "A Single Crime Has Been Removed", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), crimesToBeDeletedNum + " Crimes Have Been Removed", Toast.LENGTH_SHORT).show();

                        CrimeLab.get(getActivity()).deleteCrimes(mAdapter.getSelectedCrimes(mAdapter.getSelectedItems()));

                        mAdapter.clearSelections();
                        actionMode.finish();
                        updateUI();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mActionMode = null;
                mAdapter.clearSelections();
            }
        };
    }
    //----------------------------------------------------------------------------------------------
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;
        private HashMap mSelectedArray;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
            mSelectedArray = new HashMap();
            viewList = new HashMap();
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount(){ return mCrimes.size(); }

        public void setCrimes(List<Crime> crimes){ mCrimes = crimes; }

        public boolean toggleSelected(int position){
            if(mSelectedArray.get(position) == null){
                mSelectedArray.put(position, true);
                return true;
            }
            else{
                mSelectedArray.remove(position);
                return false;
            }
        }

        public void clearSelections(){
            mSelectedArray.clear();

            for(int i = 0; i < mCrimes.size(); i ++){
                View view = (View)viewList.get(i);
                if(view != null)
                    view.setActivated(false);
            }

            notifyDataSetChanged();
        }

        public int getSelectedItemCount(){
            return mSelectedArray.size();
        }

        public List<Integer> getSelectedItems(){
            List<Integer> selectedItems = new ArrayList<>(mSelectedArray.size());
            for(int i = 0; i < mCrimes.size(); i ++){
                if(mSelectedArray.get(i) != null)
                    selectedItems.add(i);
            }
            return selectedItems;
        }

        public List<Crime> getSelectedCrimes(List<Integer> targets){
            List<Crime> toBeDeletedCrimes = new ArrayList<>(targets.size());
            for(int t: targets)
                toBeDeletedCrimes.add(mCrimes.get(t));

            return toBeDeletedCrimes;
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
        checkEmptyView();
    }
    //----------------------------------------------------------------------------------------------
    private void updateSubtitle(){
        String subtitle;
        if(mSubtitleVisible){
            int crimeNum = CrimeLab.get(getActivity()).getCrimes().size();
            subtitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeNum, crimeNum);
//            subtitle = getString(R.string.subtitle_format, CrimeLab.get(getActivity()).getCrimes().size());
        }
        else
            subtitle = null;

        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }
    //----------------------------------------------------------------------------------------------
    private void checkEmptyView(){
        if(mAdapter.getItemCount() == 0)
            mEmptyView.setVisibility(View.VISIBLE);
        else
            mEmptyView.setVisibility(View.GONE);
    }
    //----------------------------------------------------------------------------------------------
}

