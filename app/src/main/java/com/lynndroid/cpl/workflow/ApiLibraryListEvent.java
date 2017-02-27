package com.lynndroid.cpl.workflow;

import com.lynndroid.cpl.model.Library;

import java.util.List;

/**
 * Created by lynnzeglin on 2/22/17.
 */

    // TODO make different types of events (one for list, one for a single library etc)

public class ApiLibraryListEvent {

    private List<Library> mList;

    ApiLibraryListEvent(List<Library> list){
        mList = list;
    }

    public List<Library> getList() {
        return mList;
    }

}
