package com.jiang.android.architecture;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by jiang on 2016/11/23.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        initFresco();
    }

    private void initFresco() {
        Fresco.initialize(this);
    }
}
