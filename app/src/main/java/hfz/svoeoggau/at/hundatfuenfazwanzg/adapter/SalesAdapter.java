package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class SalesAdapter extends BaseAdapter {

    public SalesAdapter(Context context, int layoutResource, Vector<?> items) {
        super(context, layoutResource, items);
    }

    public SalesAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sale sale = (Sale)getItem(position);
        sale.calc();

        TextView avatar = (TextView)holder.layout.findViewById(R.id.avatar);
        TextView textName = (TextView) holder.layout.findViewById(R.id.textName);
        TextView textPrice = (TextView) holder.layout.findViewById(R.id.textPrice);
        TextView textArticles = (TextView) holder.layout.findViewById(R.id.textArticles);

        if(sale.getPerson() != null) {
            avatar.setText(Person.getShortName(sale.getPersonLastName(), sale.getPersonFirstName()));
            textName.setText(Person.getName(sale.getPersonLastName(), sale.getPersonFirstName()));
        }
        else {
            textName.setText(getContext().getResources().getString(R.string.person_direct));
            avatar.setText("D");
        }

        textArticles.setText(sale.getArticlesText());
        textPrice.setText(Format.doubleToCurrency(sale.getSum()));

        if(sale.getPayed()==1)
            textPrice.setTextColor(context.getResources().getColor(R.color.colorDone));
        else
            textPrice.setTextColor(context.getResources().getColor(R.color.colorOpen));

        showLastItemSpacer(holder,position);
    }
}
