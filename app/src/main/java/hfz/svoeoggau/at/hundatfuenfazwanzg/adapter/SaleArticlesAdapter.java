package hfz.svoeoggau.at.hundatfuenfazwanzg.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class SaleArticlesAdapter extends BaseAdapter {

    public interface IOnPosActionListener {
        void onAdd(String articleId);
        void onRemove(String articleId);
    }

    private IOnPosActionListener posActionListener;

    public SaleArticlesAdapter(Context context, int layoutResource, Vector<?> items, IOnPosActionListener posActionListener) {
        super(context, layoutResource, items, null);
        this.posActionListener = posActionListener;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SaleArticle saleArticle = (SaleArticle)getItem(position);

        TextView textAmount = (TextView)holder.layout.findViewById(R.id.textAmount);
        TextView avatarAdd = (TextView)holder.layout.findViewById(R.id.avatarAdd);
        TextView avatarRemove = (TextView)holder.layout.findViewById(R.id.avatarRemove);
        TextView textArticle = (TextView)holder.layout.findViewById(R.id.textArticle);
        TextView textPrice = (TextView)holder.layout.findViewById(R.id.textPrice);

        avatarAdd.setTag(saleArticle.getArticleId());
        avatarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posActionListener.onAdd(view.getTag().toString());
            }
        });
        avatarRemove.setTag(saleArticle.getArticleId());
        avatarRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posActionListener.onRemove(view.getTag().toString());
            }
        });

        textAmount.setText(String.valueOf((int)saleArticle.getAmount()));
        textArticle.setText(saleArticle.getArticleText());
        textPrice.setText(Format.doubleToCurrency(saleArticle.getSumPrice()));

        showLastItemSpacer(holder,position);
    }
}
