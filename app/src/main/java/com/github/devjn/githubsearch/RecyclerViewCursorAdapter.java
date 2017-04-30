package com.github.devjn.githubsearch;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.devjn.githubsearch.utils.DataProvider;
import com.github.devjn.githubsearch.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @author Jahongir on 15-Jun-15
 * devjn@jn-arts.com
 * RecyclerViewCursorAdapter
 */

public class RecyclerViewCursorAdapter<DataType, LayoutBinding extends ViewDataBinding>
        extends RecyclerView.Adapter<RecyclerViewCursorAdapter.SimpleViewHolder<DataType, LayoutBinding>> implements View.OnClickListener {

    private static final int MAIN_TYPE = 0;

    private int itemLayout;
    private Cursor mCursor;

    private OnItemClickListener listener;
    private Context ctx;
    private SparseBooleanArray selectedItems;
    private OnViewHolderInflated mOnViewHolderInflate;

    /**
     * Standard constructor.
     *
     * @param context   The context where the RecyclerView associated with this
     *                  SimpleListItemFactory is running
     * @param itemLayout resource identifier of a layout file that defines the views
     *                  for this list item. The layout file should include at least
     *                  those named views defined in "to"
     * @param cursor    The database cursor.  Can be null if the cursor is not available yet.
     */
    public RecyclerViewCursorAdapter(Context context, @Nullable Cursor cursor, int itemLayout) {
        this.ctx = context;
        this.mCursor = cursor;
        this.itemLayout = itemLayout;
        selectedItems = new SparseBooleanArray();
    }


    @Override
    public SimpleViewHolder<DataType, LayoutBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        if (mOnViewHolderInflate != null) mOnViewHolderInflate.onInflated(view, parent, viewType);
        return new SimpleViewHolder<DataType, LayoutBinding>(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder<DataType, LayoutBinding> holder, int position) {
        if (mCursor == null) return;
        mCursor.moveToPosition(position);

    }

    public User getUserFromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndex(DataProvider.BookmarkTags.INSTANCE.getUSER_ID()));
        String login = c.getString(c.getColumnIndex(DataProvider.BookmarkTags.INSTANCE.getLOGIN_NAME()));
        String url = c.getString(c.getColumnIndex(DataProvider.BookmarkTags.INSTANCE.getURL()));
        String avatar = c.getString(c.getColumnIndex(DataProvider.BookmarkTags.INSTANCE.getAVATAR_URL()));
        return new User(id, login, url, avatar);
    }

    public int getItemType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }


    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        mCursor = newCursor;
        notifyDataSetChanged();
        return mCursor;
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick((RecyclerView.ViewHolder) v.getTag());
        }
    }


    public static class SimpleViewHolder<T, V extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private V mViewDataBinding;

        public V getViewDataBinding() {
            return mViewDataBinding;
        }

        public T getItem() {
            return mItem;
        }

        private T mItem;

        protected void setItem(final T item) {
            mItem = item;
        }

        public SimpleViewHolder(final View itemView) {
            super(itemView);
            mViewDataBinding = DataBindingUtil.bind(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return getItemType(position);
    }


    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder view);
    }

}
