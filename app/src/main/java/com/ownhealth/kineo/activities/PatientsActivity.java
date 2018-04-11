package com.ownhealth.kineo.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.patients.PatientsFragment;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;
import com.ownhealth.kineo.utils.ToolbarHelper;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsActivity extends AppCompatActivity {


    private PatientsViewModel mPatientsViewModel;
    private SearchView mSearchView;
    private NestedScrollView mScrollView;
    private String mSearchTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            PatientsFragment fragment = new PatientsFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, PatientsFragment.TAG).commit();
        }

        mScrollView = findViewById(R.id.search_scrollview);
        initToolbar();
        mPatientsViewModel = ViewModelProviders.of(this).get(PatientsViewModel.class);
    }

    /**
     * Creates an Intent for this activity, in case in the future we want to call this activity and perform automatically a search,
     * we should pass a query string as a parameter, and call SearchCategoryResultsFragment's handleSearch method.
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, PatientsActivity.class);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_search_results);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ToolbarHelper.setToolbar(this, toolbar);
        ToolbarHelper.show(this, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * Called when performing a search from the searchView, calls SearchCategoryResultsFragment handleQuery
//     * method in order to populate lists and clears focus from searchView
//     */
//    @Override
//    protected void onNewIntent(Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            mSearchTerms = intent.getStringExtra(SearchManager.QUERY);
//            handleSearch(mSearchTerms);
//            mSearchView.clearFocus();
//            mSearchView.onActionViewExpanded();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        return true;
    }

//    /**
//     * Handles searchQuery adding size and filters and performs the search.
//     *
//     * @param searchQuery searchQuery including key words user wants to search for
//     */
//    public void handleSearch(String searchQuery) {
//        SearchResultsViewModel.loadSearchResults(searchQuery);
//    }
//
//    /**
//     * Receives searchResults, filters them into categories and inflates a Fragment for each of them. Also recieves the event to
//     * show all the results for top results, superstars or a specified category after view all button is clicked.
//     */
//    @Override
//    public void accept(EventAction eventAction) throws Exception {
//        if (ACTION_SEARCH_POPULATE == eventAction.getAction()) {
//            SearchQuery searchQuery = (SearchQuery) eventAction.getData();
//            mScrollView.fullScroll(ScrollView.FOCUS_UP);
//            FragmentTransaction fragmentTransaction = removePreviousFragmentsAndPrepareForNew();
//            List<Bucket> bucketsList = searchQuery != null ? searchQuery.getFacets().getCategoriesFacet().getBuckets() : null;
//            if (bucketsList != null) {
//                for (int i = -2; i < bucketsList.size(); i++) {
//                    SearchCategoryResultsFragment searchCategoryResultsFragment;
//                    ArrayList<Hit> hits = (ArrayList<Hit>) searchQuery.getHits().getHits();
//                    ArrayList<Hit> filteredList = new ArrayList<>();
//                    switch (i) {
//                        case CATEGORY_TOP_RESULTS:
//                            searchCategoryResultsFragment = SearchCategoryResultsFragment.newInstance(getString(R.string.search_top_results), hits, false, hits.size());
//                            break;
//                        case CATEGORY_SUPERSTARS:
//                            for (Hit hit : hits) {
//                                if (hit.getFields().getCtype() != null && getString(R.string.talent).equals(hit.getFields().getCtype())) {
//                                    filteredList.add(hit);
//                                }
//                            }
//                            searchCategoryResultsFragment = SearchCategoryResultsFragment.newInstance(getString(R.string.superstars), filteredList, false, filteredList.size());
//                            break;
//                        default:
//                            for (Hit hit : hits) {
//                                if (hit.getFields().getCategories() != null && hit.getFields().getCategories().get(0).equals(bucketsList.get(i).getValue())) {
//                                    filteredList.add(hit);
//                                }
//                            }
//                            searchCategoryResultsFragment = SearchCategoryResultsFragment.newInstance(bucketsList.get(i).getValue(), filteredList, false, bucketsList.get(i).getCount());
//                            break;
//                    }
//                    fragmentTransaction.add(R.id.fragment_search_categories_container, searchCategoryResultsFragment);
//                }
//                fragmentTransaction.commit();
//            }
//        } else if (ACTION_SEARCH_POPULATE_CATEGORY == eventAction.getAction()) {
//            mScrollView.fullScroll(ScrollView.FOCUS_UP);
//            SearchQuery searchQuery = (SearchQuery) eventAction.getData();
//            ArrayList<Hit> hits = (ArrayList<Hit>) searchQuery.getHits().getHits();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            SearchCategoryResultsFragment searchCategoryResultsFragment;
//            searchCategoryResultsFragment = SearchCategoryResultsFragment.newInstance(hits.get(0).getFields().getCategories().get(0), hits, true, 0);
//            fragmentTransaction.replace(R.id.fragment_search_categories_container, searchCategoryResultsFragment).addToBackStack(null);
//            fragmentTransaction.commit();
//        }
//    }
//
//    private FragmentTransaction removePreviousFragmentsAndPrepareForNew() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStackImmediate();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        List<Fragment> fragments = fragmentManager.getFragments();
//        for (Fragment fragment : fragments) {
//            fragmentTransaction.remove(fragment);
//        }
//        return fragmentTransaction;
//    }
//
//    public void populateTopResultsOrSuperstars(String categoryName, ArrayList<Hit> hits) {
//        mScrollView.fullScroll(ScrollView.FOCUS_UP);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        SearchCategoryResultsFragment searchCategoryResultsFragment;
//        searchCategoryResultsFragment = SearchCategoryResultsFragment.newInstance(categoryName, hits, true, 0);
//        fragmentTransaction.replace(R.id.fragment_search_categories_container, searchCategoryResultsFragment).addToBackStack(null);
//        fragmentTransaction.commit();
//    }

    public String getSearchTerms() {
        return mSearchTerms;
    }

}
