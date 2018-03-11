package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.SaleDayAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.SalesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.classes.SaleDay;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;

/**
 * Created by Christian on 11.03.2018.
 */

public class SaleMenuActivity extends AuthedActivity {

    private SaleDayAdapter mAdapter;
    private BaseList mList;
    private Context context;
    private ListenerRegistration listenerRegistration;
    static final int CHOOSE_SALE = 0;
    private Vector<Sale> sales = new Vector<>();
    private Vector<SaleDay> saleDays = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salemenu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        mAdapter = new SaleDayAdapter(context, R.layout.listitem_day, saleDays, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                chooseDay(saleDays.get(position).getDay());
            }
        });

        showProgress();
        listenerRegistration = Sale.listenAll(sales, context, new Sale.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createDayList();
                if(mList == null)
                    mList = new BaseList(context, R.id.swipeRefreshLayout, mAdapter);

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void createDayList() {
        saleDays.clear();
        for(Sale sale : sales) {
            SaleDay day = SaleDay.getDay(saleDays, sale.getDayStr());
            if(day == null) {
                day = new SaleDay(sale.getDayStr(), 0, 0);
                saleDays.add(day);
            }
            day.addSale(sale);
        }
    }

    private void chooseDay(String day) {
        Intent ret = new Intent();
        ret.putExtra("day",day);
        this.setResult(RESULT_OK, ret);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration != null)
            listenerRegistration.remove();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
