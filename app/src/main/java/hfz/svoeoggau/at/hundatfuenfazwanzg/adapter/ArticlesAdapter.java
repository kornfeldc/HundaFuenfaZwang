package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class ArticlesAdapter extends BaseAdapter {

    public ArticlesAdapter(Context context, int layoutResource, Vector<?> items) {
        super(context, layoutResource, items);
    }

    public ArticlesAdapter(Context context, int layoutResource, Vector<?> items, IOnItemClickListener onItemClickListener) {
        super(context, layoutResource, items, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = (Article)getItem(position);

        TextView textTitle = (TextView) holder.layout.findViewById(R.id.textTitle);
        textTitle.setText(article.getTitle());

        TextView textPrice = (TextView)holder.layout.findViewById(R.id.textPrice);
        textPrice.setText(Format.doubleToCurrency(article.getPrice()));

        ImageView imageFavorite = (ImageView)holder.layout.findViewById(R.id.imageFavorite);
        imageFavorite.setVisibility(article.getFavorite() != 0 ? View.VISIBLE: View.GONE);

        showLastItemSpacer(holder,position);
    }
}
