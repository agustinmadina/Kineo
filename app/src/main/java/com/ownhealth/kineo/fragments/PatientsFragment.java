package com.ownhealth.kineo.fragments;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.adapter.PatientAdapter;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.LocalPatientRepository;
import com.ownhealth.kineo.utils.ToolbarHelper;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String TAG = "PatientsFragment";
    private PatientsViewModel mPatientsViewModel;
    private PatientAdapter mPatientAdapter;

    @BindView(R.id.search_view)
    SearchView mSearchView;
    @BindView(R.id.fast_scroller_recycler)
    IndexFastScrollRecyclerView mRecyclerView;
    @BindView(R.id.fab_add_patient)
    FloatingActionButton mFabAddPatient;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PatientsViewModel.Factory factory = new PatientsViewModel.Factory(getActivity().getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getActivity().getApplication()).patientDao()));
        mPatientsViewModel = ViewModelProviders.of(getActivity(), factory).get(PatientsViewModel.class);
        mPatientsViewModel.getPatients().observe(this, patients -> mPatientAdapter.setPatientList(patients));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);
        ButterKnife.bind(this, view);

        setupRecyclerView();
        setupSearchView();
        initToolbar(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchView.setQuery("", true);
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(this);
    }

    private void setupRecyclerView() {
        mPatientAdapter = new PatientAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPatientAdapter);
        mRecyclerView.setIndexTextSize(12);
        mRecyclerView.setIndexBarColor("#33334c");
        mRecyclerView.setIndexBarCornerRadius(0);
        mRecyclerView.setIndexBarTransparentValue((float) 0.4);
        mRecyclerView.setIndexbarMargin(0);
        mRecyclerView.setIndexbarWidth(40);
        mRecyclerView.setPreviewPadding(0);
        mRecyclerView.setIndexBarTextColor("#FFFFFF");

        mRecyclerView.setIndexBarVisibility(true);
        mRecyclerView.setIndexbarHighLateTextColor("#33334c");
        mRecyclerView.setIndexBarHighLateTextVisibility(true);
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
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

    @OnClick(R.id.fab_add_patient)
    public void addPatient() {
        AddPatientFragment addPatientFragment = new AddPatientFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addPatientFragment, AddPatientFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!query.equals("")) {
            mPatientAdapter.getFilter().filter(query);
        }
        return false;
    }
}
