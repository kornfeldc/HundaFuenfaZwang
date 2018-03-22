package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.classes.SumData;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;
import hfz.svoeoggau.at.hundatfuenfazwanzg.holograph.BarGraph;
import hfz.svoeoggau.at.hundatfuenfazwanzg.holograph.PieGraph;
import hfz.svoeoggau.at.hundatfuenfazwanzg.views.ChartLegend;

/**
 * Created by Christian on 23.02.2018.
 */

public class StatisticsFragment extends BaseFragment {

    private FloatingActionButton buttonPie, buttonChart;
    private RadioButton radioPersons, radioArticles;
    private EditText textDateFrom, textDateTo;
    private BarGraph barChart;
    private PieGraph pieChart;
    private ChartLegend chartLegend;

    private static int MAX_ITEMS = 10;

    private enum ChartMode {
        PIE,
        CHART
    }

    private enum ContentMode {
        PERSONS,
        ARTICLES
    }

    private ChartMode chartMode = ChartMode.CHART;
    private ContentMode contentMode = ContentMode.PERSONS;

    private ListenerRegistration listenerRegistration;

    private Vector<Sale> sales = new Vector<>();
    private Vector<Sale> salesFiltered = new Vector<>();
    private Vector<SumData> sumDataPersons = new Vector<>();
    private Vector<SumData> sumDataArticles = new Vector<>();

    private Calendar dateFrom = DF.StringToCalendar("01.01.2000", "dd.MM.yyyy"), dateTo = DF.StringToCalendar("01.01.2999", "dd.MM.yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonPie = (FloatingActionButton)getView().findViewById(R.id.buttonPie);
        buttonChart = (FloatingActionButton)getView().findViewById(R.id.buttonChart);
        radioPersons = (RadioButton)getView().findViewById(R.id.radioPersons);
        radioArticles = (RadioButton)getView().findViewById(R.id.radioArticles);
        textDateFrom = (EditText)getView().findViewById(R.id.textDateFrom);
        textDateTo = (EditText)getView().findViewById(R.id.textDateTo);
        barChart = (BarGraph) getView().findViewById(R.id.barChart);
        pieChart = (PieGraph) getView().findViewById(R.id.pieChart);
        chartLegend = (ChartLegend)getView().findViewById(R.id.chartLegend);
        buttonPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMode = ChartMode.PIE;
                loadUI();
            }
        });
        buttonChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMode = ChartMode.CHART;
                loadUI();
            }
        });
        radioPersons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    contentMode = ContentMode.PERSONS;
                    loadUI();
                }
            }
        });
        radioArticles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    contentMode = ContentMode.ARTICLES;
                    loadUI();
                }
            }
        });
        textDateFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadUI();
            }
        });
        textDateTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadUI();
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

        listenerRegistration = Sale.listenAll(sales, getContext(), new Sale.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                loadUI();
            }
        });
    }

    private void createFiltered() {
        salesFiltered.clear();
        parseDates();
        for(Sale s : sales) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(s.getDay());
            if(DF.IsBetween(cal, dateFrom, dateTo)) {
                salesFiltered.add(s);
            }
        }
        makeChartData();
    }

    private void makeChartData() {

        //make sumdata
        sumDataArticles.clear();
        sumDataPersons.clear();
        for (Sale s : salesFiltered) {
            String personName = Person.getName(s.getPersonLastName(), s.getPersonFirstName(), s.getPersonLinkName());

            SumData psd = new SumData();
            boolean found = false;
            for(SumData psdf : sumDataPersons) {
                if(psdf.getTitle().equals(personName)) {
                    psd = psdf;
                    found = true;
                }
            }
            psd.setValue(psd.getValue()+s.getSum()+s.getTip());
            psd.setTitle(personName);
            if(!found)
                sumDataPersons.add(psd);

            List<SaleArticle> articles = s.getArticles();
            for(SaleArticle article : articles) {
                String articleName = article.getArticleText();

                SumData asd = new SumData();
                boolean founda = false;
                for(SumData asdf : sumDataArticles) {
                    if(asdf.getTitle().equals(articleName)) {
                        asd = asdf;
                        founda = true;
                    }
                }
                asd.setValue(asd.getValue()+article.getSumPrice());
                asd.setTitle(articleName);
                if(!founda)
                    sumDataArticles.add(asd);
            }
        }



        if(chartMode == ChartMode.CHART) {



            /*List<BarEntry> entries = new ArrayList<>();

            float x = 0;
            for(SumData sd : sumDataArticles) {
                BarEntry barEntry = new BarEntry(x, sd.getValue().floatValue());
                entries.add(barEntry);
                x++;
            }

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return sumDataPersons.get((int) value).getTitle();
                }
            };

            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            BarDataSet set = new BarDataSet(entries, "BarDataSet");
            BarData data = new BarData(set);
            barChart.setData(data);
            barChart.setFitBars(true);
            barChart.invalidate();*/
        }
        else {

        }
    }

    private void parseDates() {
        String df = textDateFrom.getText().toString();
        String dt = textDateTo.getText().toString();
        dateFrom = DF.StringToCalendar( df.isEmpty() ? "01.01.2000" : df, "dd.MM.yyyy");
        dateTo = DF.StringToCalendar( dt.isEmpty() ? "01.01.2999" : dt, "dd.MM.yyyy");
    }

    private void loadUI() {
        if(chartMode == ChartMode.CHART) {
            buttonChart.hide();
            buttonPie.show();
        }
        else {
            buttonChart.show();
            buttonPie.hide();
        }

        barChart.setVisibility(chartMode == ChartMode.CHART ? View.VISIBLE : View.GONE);
        pieChart.setVisibility(chartMode == ChartMode.PIE ? View.VISIBLE : View.GONE);
        radioPersons.setChecked(contentMode == ContentMode.PERSONS);
        radioArticles.setChecked(contentMode == ContentMode.ARTICLES);

        createFiltered();
    }
}
