package com.ownhealth.kineo.persistence;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Agustin Madina on 09/12/18.
 */
public class MyFileProvider extends android.support.v4.content.FileProvider {

    public Uri getDatabaseURI(Context c) {
        // https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        File data = Environment.getDataDirectory();
        String dbName = "PhysioAssist.db";
        String currentDBPath = "//data//com.ownhealth.kineo//databases//" + dbName;

        File exportFile = new File(data, currentDBPath);

        return getFileUri(c, exportFile);
    }

    public Uri getFileUri(Context c, File f){
        return getUriForFile(c, "com.ownhealth.kineo.persistence.MyFileProvider", f);
    }

}