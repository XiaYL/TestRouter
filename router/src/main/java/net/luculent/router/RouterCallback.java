package net.luculent.router;

import android.content.Context;
import android.net.Uri;

/**
 * Created by xiayanlei on 2017/3/10.
 */

public interface RouterCallback {

    boolean beforeOpen(Context context, Uri uri);

    void notFound(Context context, Uri uri);

    void afterOpen(Context context, Uri uri);
}
