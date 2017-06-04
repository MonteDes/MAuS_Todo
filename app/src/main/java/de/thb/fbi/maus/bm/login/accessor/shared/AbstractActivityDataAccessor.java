package de.thb.fbi.maus.bm.login.accessor.shared;

import android.app.Activity;

/**
 * Created by Bene on 11.05.2017.
 */
public abstract class AbstractActivityDataAccessor {
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
