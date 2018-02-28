package hfz.svoeoggau.at.hundatfuenfazwanzg.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;


/**
 * Created by Christian on 30.10.2014.
 */
public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    int layoutResource = 0;
    Vector<?> items;
    IOnItemClickListener onItemClickListener;
    Context context;

    public interface IOnItemClickListener {
        <T> void onItemClick(ViewGroup viewGroup, View view, int position, T item);
    }

    public BaseAdapter(Context context, int layoutResource, Vector<?> items) {
        this.items = items;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    public BaseAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResource, viewGroup, false);
        if(onItemClickListener != null) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewGroup.indexOfChild(v);
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(viewGroup, v, position, items.get(position));
                }
            });
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public <T> T getItem(int i) {
        return (T)items.get(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;

        public ViewHolder(View layout) {
            super(layout);
            this.layout = layout;
        }
    }

    public Context getContext() {
        return this.context;
    }
}