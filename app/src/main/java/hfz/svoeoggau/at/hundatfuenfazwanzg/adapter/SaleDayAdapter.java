package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.classes.SaleDay;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class SaleDayAdapter extends BaseAdapter {

    public SaleDayAdapter(Context context, int layoutResource, Vector<?> items) {
        super(context, layoutResource, items);
    }

    public SaleDayAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SaleDay saleDay = (SaleDay)getItem(position);

        TextView textDay = (TextView)holder.layout.findViewById(R.id.textDay);
        TextView textToPay = (TextView)holder.layout.findViewById(R.id.textToPay);
        TextView textPayed = (TextView)holder.layout.findViewById(R.id.textPayed);

        textDay.setText(saleDay.getDay());
        if(saleDay.getTopay() > 0) {
            textToPay.setVisibility(View.VISIBLE);
            textToPay.setText(Format.doubleToCurrency(saleDay.getTopay()));
        }
        else
            textToPay.setVisibility(View.GONE);

        textPayed.setText(Format.doubleToCurrency(saleDay.getPayed()));

        showLastItemSpacer(holder,position);
    }
}
