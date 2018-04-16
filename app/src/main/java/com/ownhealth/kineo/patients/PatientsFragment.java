package com.ownhealth.kineo.patients;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.activities.AddPatientFragment;
import com.ownhealth.kineo.adapter.PatientAdapter;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.LocalPatientRepository;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String TAG = "PatientsFragment";
    private PatientsViewModel mPatientsViewModel;
    private PatientAdapter mPatientAdapter;

    @BindView(R.id.search_view)
    SearchView mSearchView;
    @BindView(R.id.recycler_view_list_patients)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_add_patient)
    FloatingActionButton mFabAddPatient;

    private String mSearchTerms;

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
        return view;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientAdapter = new PatientAdapter(getContext());
        mRecyclerView.setAdapter(mPatientAdapter);
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
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
        mPatientAdapter.getFilter().filter(query);
        return false;
    }
}
