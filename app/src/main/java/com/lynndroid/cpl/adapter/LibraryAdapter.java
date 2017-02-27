package com.lynndroid.cpl.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lynndroid.cpl.R;
import com.lynndroid.cpl.model.vieworiented.LibraryListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for turning the library model data into pretty views!
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    // interface to handle tapping on library in list
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private List<LibraryListItem> mDataset;
    private ItemClickListener mListener;

    public LibraryAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<LibraryListItem> libraryListData) {
        mDataset.clear();
        mDataset.addAll(libraryListData);

        // NOTE: mDataset = libraryListData; is not the same thing as the above lines ^^

        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_library, parent, false);
        return(new ViewHolder(v));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // get data for the position and replace it in the view, set item click listener as well
        final String name = mDataset.get(position).getName();
        final String addr = mDataset.get(position).getAddress();
        holder.txtLibName.setText(name);
        holder.txtLibAddr.setText(addr);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // set listener that gets called when list items are tapped
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    // view holder provides access to the all the views for each data item
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtLibName;
        TextView txtLibAddr;

        ViewHolder(View v) {
            super(v);
            txtLibName = (TextView) v.findViewById(R.id.library_name);
            txtLibAddr = (TextView) v.findViewById(R.id.library_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // call the onClick in the OnItemClickListener
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}