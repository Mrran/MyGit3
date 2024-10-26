package com.matou.smartcar.global;

import android.content.Context;
import androidx.core.content.FileProvider;

public class ZQYProvider extends FileProvider {
    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }
}
