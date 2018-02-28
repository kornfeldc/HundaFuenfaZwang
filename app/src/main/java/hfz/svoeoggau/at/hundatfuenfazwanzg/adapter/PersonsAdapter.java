package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;

/**
 * Created by Christian on 25.02.2018.
 */

public class PersonsAdapter extends BaseAdapter {

    private String search = "";

    public PersonsAdapter(Context context, int layoutResource, Vector<?> items) {
        super(context, layoutResource, items);
    }

    public PersonsAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = (Person)getItem(position);
        LinearLayout layout = (LinearLayout)holder.layout.findViewById(R.id.listLayout);
        if(person.matchSearch(search)) {
            layout.setVisibility(View.VISIBLE);
            TextView textLastName = (TextView) holder.layout.findViewById(R.id.textLastName);
            textLastName.setText(person.getLastName());
        }
        else
            layout.setVisibility(View.GONE);
    }
}
