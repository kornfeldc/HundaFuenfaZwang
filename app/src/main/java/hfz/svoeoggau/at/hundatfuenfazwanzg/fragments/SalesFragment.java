package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.PersonActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.PersonChooserActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.SaleActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.SaleMenuActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.SalesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Christian on 23.02.2018.
 */

public class SalesFragment extends BaseFragment {

    private SalesAdapter mAdapter;
    private BaseList mList;
    private Vector<Sale> sales = new Vector<>();
    private Vector<Sale> salesFiltered = new Vector<>();
    private CardView cardDay;
    private TextView textDay, textNoSale;
    private FloatingActionButton fab;
    private String search = "";
    private ListenerRegistration listenerRegistration;
    private String day = DF.CalendarToString();
    private ImageView imageMenu;

    private static final int CHOOSE_DAY = 0;

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

        textNoSale = (TextView)getView().findViewById(R.id.textNoSale);
        textDay = (TextView)getView().findViewById(R.id.textDay);
        cardDay = (CardView)getView().findViewById(R.id.cardDay);
        cardDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar act = DF.StringToCalendar(day, "dd.MM.yyyy");

                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        day = DF.CalendarToString(newDate);
                        listen();
                    }

                }, act.get(Calendar.YEAR), act.get(Calendar.MONTH), act.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        imageMenu = (ImageView)getView().findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SaleMenuActivity.class);
                startActivityForResult(intent, CHOOSE_DAY);
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

        listen();
    }

    private void listen() {
        showProgress();
        if(listenerRegistration != null) {
            listenerRegistration.remove();
            sales.clear();
        }

        textDay.setText(day);

        listenerRegistration = Sale.listen(sales, day, getContext(), new Sale.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createFiltered();
                if(mList == null) {
                    mList = new BaseList(getActivity(), R.id.swipeRefreshLayout, mAdapter);
                    mList.hideFabOnScroll(fab);
                }

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

        if(salesFiltered.size() == 0)
            textNoSale.setVisibility(View.VISIBLE);
        else
            textNoSale.setVisibility(View.GONE);
    }

    private void openSale(Sale sale) {
        Intent intent = new Intent(getActivity(), SaleActivity.class);
        if(sale != null) {
            //intent.putExtra("saleId", sale.getReference().getId());
            intent.putExtra("saleId", Params.setParams(sale));
        }
        else {
            intent.putExtra("day", day);
            intent.putExtra("actSales", Params.setParams(sales));
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(listenerRegistration != null)
            listenerRegistration.remove();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSE_DAY && resultCode == RESULT_OK) {
            day = data.getStringExtra("day");
            listen();
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
