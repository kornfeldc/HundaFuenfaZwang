package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class PayActivity extends BaseActivity {

    Sale sale;
    private Context context;
    FloatingActionButton fab;
    TextView textAvatar, textName, textDay;
    CardView cardPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        textAvatar = (TextView)findViewById(R.id.avatar);
        textName = (TextView)findViewById(R.id.textName);
        textDay = (TextView)findViewById(R.id.textDay);
        cardPerson = (CardView)findViewById(R.id.cardPerson);
        fab = (FloatingActionButton)findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sale.setPayed(1);
                save();
            }
        });

        String saleId = getIntent().getStringExtra("saleId");
        if(saleId == null)
            saleId = "";

        if(!saleId.isEmpty()) {
            showProgress();
            Sale.getById(saleId, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    hideProgress();
                    sale = (Sale)obj;
                    loadUI();
                }
            });
        }
        else
            this.finish();
    }

    private void loadUI() {
        if(sale.getPerson() != null) {
            textName.setText(Person.getName(sale.getPersonLastName(), sale.getPersonFirstName()));
            textAvatar.setText(Person.getShortName(sale.getPersonLastName(), sale.getPersonFirstName()));
        }
        else {
            textName.setText(getResources().getString(R.string.person_direct));
            textAvatar.setText("D");
        }
        textDay.setText(sale.getDayStr());

        String sum = getResources().getString(R.string.sum) + ": " + Format.doubleToCurrency(sale.getSum());
    }

    private void save() {
        sale.save(null);
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
