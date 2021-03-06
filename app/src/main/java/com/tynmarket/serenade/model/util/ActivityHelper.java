package com.tynmarket.serenade.model.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.tynmarket.serenade.R;

/**
 * Created by tynmarket on 2018/03/30.
 */

public class ActivityHelper {
    private static final int REQUEST_CODE_NO_OP = 1000;

    public static void startUriActivity(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // TODO: Set only if url is twitter
        // Android 11以上の場合
        if (Build.VERSION.SDK_INT >= 30) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        startActivity(context, intent);
    }

    public static void startActivity(Context context, Intent intent) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, REQUEST_CODE_NO_OP);
            activity.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        } else {
            context.startActivity(intent);
        }
    }
}
