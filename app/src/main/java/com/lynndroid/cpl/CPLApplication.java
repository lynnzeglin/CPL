package com.lynndroid.cpl;

import android.app.Application;

import com.lynndroid.cpl.workflow.AndroidBus;
import com.lynndroid.cpl.workflow.DataAccessor;

/**
 * Application singleton
 */

public class CPLApplication extends Application {

    private static CPLApplication cplApplication;

    public static CPLApplication getInstance() {
        return cplApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        cplApplication = this;

        // instantiate the event bus
        AndroidBus.getInstance();

        // instantiate the data accessor
        DataAccessor.getInstance();
    }
}
