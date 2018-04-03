package com.ownhealth.kineo.utils;


import android.app.ActionBar;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Gregory Rasmussen on 3/31/17.
 */

public class ToolbarHelper {

    public static void setToolbar(@NonNull Activity activity, @Nullable Toolbar toolbar) {
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) activity;
            act.setSupportActionBar(toolbar);
            act.setTitle("");
        }
    }

    public static void show(@NonNull Activity activity, boolean show) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            if (show) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    public static void setTitle(@NonNull Toolbar toolbar, @IdRes int textViewId, @Nullable String title) {
        View view = toolbar.findViewById(textViewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(TextUtils.isEmpty(title) ? "" : title);
        }
    }

}
