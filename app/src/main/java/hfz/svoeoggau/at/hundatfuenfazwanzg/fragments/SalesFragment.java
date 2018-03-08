package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.PersonActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.SaleActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.SalesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;

/**
 * Created by Christian on 23.02.2018.
 */

public class SalesFragment extends BaseFragment {

    private SalesAdapter mAdapter;
    private BaseList mList;
    private Vector<Sale> sales = new Vector<>();
    private Vector<Sale> salesFiltered = new Vector<>();

    private FloatingActionButton fab;
    private String search = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = getView().findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSale(null);
            }
        });

        mAdapter = new SalesAdapter(getActivity(), R.layout.listitem_sale, salesFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                openSale(salesFiltered.get(position));
            }
        });

        setOnSearchListener(new OnSearch() {
            @Override
            public void search(String searchStr) {
                search = searchStr;
                createFiltered();
                mAdapter.notifyDataSetChanged();
            }
        });

        showProgress();
        Sale.listen(sales, new Sale.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createFiltered();
                if(mList == null)
                    mList = new BaseList(getActivity(), R.id.swipeRefreshLayout, mAdapter);

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void createFiltered() {
        salesFiltered.clear();
        for(Sale s : sales) {
            if(s.matchSearch(search))
                salesFiltered.add(s);
        }
    }

    private void openSale(Sale sale) {
        Intent intent = new Intent(getActivity(), SaleActivity.class);
        if(sale != null)
            intent.putExtra("saleId", sale.getReference().getId());
        startActivity(intent);
    }
}
