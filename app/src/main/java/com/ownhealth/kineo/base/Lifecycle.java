package com.ownhealth.kineo.base;

import android.support.annotation.NonNull;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public interface Lifecycle {

    interface View {

    }

    interface ViewModel {

        void onViewResumed();
        void onViewAttached(@NonNull Lifecycle.View viewCallback);
        void onViewDetached();
    }
}
