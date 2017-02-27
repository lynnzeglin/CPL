package com.lynndroid.cpl.workflow;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Singleton provider for otto bus
 */

public class AndroidBus extends Bus {

    private static AndroidBus sBus = null;
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    // NOTE: default constructor utilizes ThreadEnforcer.MAIN
    // make another constructor if you want to configure another ThreadEnforcer
    public AndroidBus() {
        super();
    }

    // singleton getter
    public static AndroidBus getInstance() {

        if (sBus == null) {
            sBus = new AndroidBus();
        }

        return sBus;
    }

    // post event on main thread since subscribers are registered there
    @Override
    public void post(final Object event) {
        Log.d("CPL", "+++ overridden post called from AndroidBus class");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        }
        else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    AndroidBus.super.post(event);
                }
            });
        }
    }
}
