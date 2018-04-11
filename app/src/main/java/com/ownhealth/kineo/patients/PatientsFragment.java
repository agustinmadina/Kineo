package com.ownhealth.kineo.patients;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.LocalPatientRepository;
import com.ownhealth.kineo.utils.ToolbarHelper;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsFragment extends Fragment {

    public static final String TAG =  "PatientsFragment";
    private PatientsViewModel mPatientsViewModel;

    @BindView(R.id.search_scrollview) NestedScrollView mScrollView;
    private SearchView mSearchView;
    private String mSearchTerms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PatientsViewModel.Factory factory = new PatientsViewModel.Factory(getActivity().getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getActivity().getApplication()).patientDao()));
        mPatientsViewModel = ViewModelProviders.of(getActivity(), factory).get(PatientsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);
        ButterKnife.bind(this, view);

        initToolbar(view);
        return view;
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_search_results);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ToolbarHelper.setToolbar(getActivity(), toolbar);
        ToolbarHelper.show(getActivity(), true);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        searchMenuItem.expandActionView();
        mSearchView.clearFocus();
    }
}
