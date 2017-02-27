package com.lynndroid.cpl.workflow;

import com.lynndroid.cpl.model.vieworiented.LibraryListItem;

import java.util.List;

/**
 * Created by lynnzeglin on 2/27/17.
 */

public class ViewOrientedLibraryListEvent {
    private List<LibraryListItem> mList;

    ViewOrientedLibraryListEvent(List<LibraryListItem> list){
        mList = list;
    }

    public List<LibraryListItem> getList() {
        return mList;
    }
}
