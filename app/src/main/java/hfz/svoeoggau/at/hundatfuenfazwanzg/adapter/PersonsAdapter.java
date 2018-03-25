package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
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

    public PersonsAdapter(Context context, int layoutResource, Vector<?> items) {
        super(context, layoutResource, items);
    }

    public PersonsAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = (Person)getItem(position);

        TextView textName = (TextView) holder.layout.findViewById(R.id.textName);
        textName.setText(person.getName());

        TextView textAvatar = (TextView)holder.layout.findViewById(R.id.avatar);
        textAvatar.setText(person.getShortName());

        ImageView imageMember = (ImageView)holder.layout.findViewById(R.id.imageMember);
        imageMember.setVisibility(person.getMember() != 0 ? View.VISIBLE: View.GONE);

        showLastItemSpacer(holder,position);
    }
}
