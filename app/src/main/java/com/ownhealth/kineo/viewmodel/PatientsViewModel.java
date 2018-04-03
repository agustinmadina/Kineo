package com.ownhealth.kineo.viewmodel;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsViewModel {

    private static final String TAG = "SearchResultsViewModel";
//    private static final DisposableHelper mDisposableHelper = new DisposableHelper();

    public PatientsViewModel() {
    }

//    public void tearDown() {
//        mDisposableHelper.dispose();
//    }

    /**
     * Performs API request and, if successful, populates search lists.
     *
     * @param searchTerms complete URL including searchTerms, size and filters, to be added to the BASE_URL
     */
//    public static void loadSearchResults(@NotNull String searchTerms) {
//        final Store<SearchQuery, StoreRequest> store;
//        store = getInstance().openSearchQueryStore(searchTerms);
//        mDisposableHelper.add(store.get(new StoreRequest())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(searchQuery -> {
//                    RxBus.getInstance().post(new EventAction<>(ACTION_SEARCH_POPULATE, searchQuery));
//                }, throwable -> {
//                    Log.e(TAG, "onError: Unable to get search query ", throwable);
//                }));
//    }

    /**
     * Performs API request and, if successful, populates category full list.
     *
     * @param searchTerms  complete URL including searchTerms, size and filters, to be added to the BASE_URL
     * @param categoryName is added to the request in order to bring only that category results
     * @param size number of results you want to request for
     */
//    public static void loadCategoryResults(@NotNull String searchTerms, @NotNull String categoryName, @NotNull int size) {
//        final Store<SearchQuery, StoreRequest> store;
//        store = getInstance().openSearchCategoryQueryStore(searchTerms, categoryName, size);
//        mDisposableHelper.add(store.get(new StoreRequest())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(searchQuery -> {
//                    RxBus.getInstance().post(new EventAction<>(ACTION_SEARCH_POPULATE_CATEGORY, searchQuery));
//                }, throwable -> {
//                    Log.e(TAG, "onError: Unable to get search category query ", throwable);
//                }));
//    }
//            }

}
