package com.jiang.android.zhihu_topanswer;

import com.jiang.android.architecture.utils.L;
import com.jiang.android.zhihu_topanswer.db.DbCore;

/**
 * Created by jiang on 2016/12/24.
 */

public class App extends com.jiang.android.architecture.App {

    @Override
    public void onCreate() {
        super.onCreate();
        DbCore.init(this);
        L.debug(true);
    }
}
