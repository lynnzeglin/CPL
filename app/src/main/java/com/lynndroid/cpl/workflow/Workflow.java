package com.lynndroid.cpl.workflow;

import android.content.Context;

import com.lynndroid.cpl.model.vieworiented.LibraryDetail;
import com.lynndroid.cpl.model.vieworiented.LibraryListItem;
import com.lynndroid.cpl.model.LookupKey;

import java.util.List;

/**
 * Created by lynnzeglin on 2/24/17.
 */

public class Workflow {

    private Context mContext;

    public Workflow(Context context) {
        mContext = context;
    }

    public List<LibraryListItem> loadLibraryList() {

        return DataAccessor.getInstance().loadLibraries(mContext);
    }

    public LibraryDetail loadLibraryDetail(LookupKey<String> lookupKey) {

        return DataAccessor.getInstance().loadLibraryDetail(mContext, lookupKey);
    }

}
