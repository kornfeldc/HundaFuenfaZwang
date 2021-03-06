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

    public String dayStr = "";

    public SalesAdapter(Context context, int layoutResource, Vector<?> items, String dayStr) {
        super(context, layoutResource, items);
        this.dayStr = dayStr;
    }

    public SalesAdapter(Context context, int layoutResource, Vector<?> items, String dayStr, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
        this.dayStr = dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sale sale = (Sale)getItem(position);
        sale.calc();

        TextView textSum = (TextView)holder.layout.findViewById(R.id.textSum);
        TextView textGroup = (TextView)holder.layout.findViewById(R.id.textGroup);
        textSum.setVisibility(View.GONE);
        textGroup.setVisibility(View.GONE);

        if(sale.getPayed() == 1) {
            if(position == 0 || ((Sale)getItem(position-1)).getPayed()!=1) {
                //first payed item => calculate payed sum
                double payedSum = 0.0;
                for(Sale s : (Vector<Sale>)items) {
                    if(s.getPayed() == 1)
                        payedSum += s.getCalculatedSum();
                }

                if(payedSum > 0) {
                    textSum.setVisibility(View.VISIBLE);
                    textSum.setTextColor(context.getResources().getColor(R.color.colorDone));
                    textSum.setText(Format.doubleToCurrency(payedSum));
                    textGroup.setVisibility(View.VISIBLE);
                    textGroup.setTextColor(context.getResources().getColor(R.color.colorDone));
                    textGroup.setText(context.getResources().getString(R.string.payed));
                }
            }
        }
        else if(position == 0) {
            double openSum = 0.0;
            for(Sale s : (Vector<Sale>)items) {
                if(s.getPayed() != 1)
                    openSum += s.getSum();
            }

            if(openSum > 0) {
                textSum.setVisibility(View.VISIBLE);
                textSum.setTextColor(context.getResources().getColor(R.color.colorOpen));
                textSum.setText(Format.doubleToCurrency(openSum));
                textGroup.setVisibility(View.VISIBLE);
                textGroup.setTextColor(context.getResources().getColor(R.color.colorOpen));
                textGroup.setText(context.getResources().getString(R.string.open_sales));
            }
        }

        TextView avatar = (TextView)holder.layout.findViewById(R.id.avatar);
        TextView textName = (TextView) holder.layout.findViewById(R.id.textName);
        TextView textPrice = (TextView) holder.layout.findViewById(R.id.textPrice);
        TextView textArticles = (TextView) holder.layout.findViewById(R.id.textArticles);
        TextView textDay = (TextView) holder.layout.findViewById(R.id.textDay);

        if(!sale.getPersonId().isEmpty()) {
            textName.setText(Person.getName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));
            avatar.setText(Person.getShortName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));
        }
        else {
            textName.setText(getContext().getResources().getString(R.string.person_direct));
            avatar.setText("D");
        }

        if(!sale.getDayStr().equals(dayStr)) {
            textDay.setVisibility(View.VISIBLE);
            textDay.setText(sale.getDayStr());
        }
        else
            textDay.setVisibility(View.GONE);

        textArticles.setText(sale.getArticlesText());
        textPrice.setText(Format.doubleToCurrency(sale.getCalculatedSum()));

        if(sale.getPayed()==1)
            textPrice.setTextColor(context.getResources().getColor(R.color.colorDone));
        else
            textPrice.setTextColor(context.getResources().getColor(R.color.colorOpen));

        showLastItemSpacer(holder,position);
    }
}
