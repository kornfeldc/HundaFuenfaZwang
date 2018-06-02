package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 25.02.2018.
 */

public class PayActivity extends AuthedActivity {

    private static final double ADDAMOUNT = 0.5;

    Sale sale;
    Person person;
    private Context context;
    //FloatingActionButton fab;
    Button buttonPay;
    TextView textAvatar, textName, textDay, textCredit, textSum;
    CardView cardPerson;
    CheckBox checkUseCredit;
    LinearLayout layoutToPay, layoutInclTip, layoutGiven, layoutRetour, layoutRemainingCredit;
    EditText textToPay, textRetour, textRemainingCredit, textInclTip, textGiven;
    TextView avatarInclTipAdd, avatarInclTipRemove, avatarGivenAdd, avatarGivenRemove;

    ListenerRegistration listenerRegistration;
    private boolean hasCredit = false;
    private boolean creditBiggerThanSum = false;

    private double topay = 0, incltip = 0, given = 0, retour = 0, remainingCredit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        textAvatar = (TextView)findViewById(R.id.avatar);
        textName = (TextView)findViewById(R.id.textName);
        textCredit = (TextView)findViewById(R.id.textCredit);
        textDay = (TextView)findViewById(R.id.textDay);
        textSum = (TextView)findViewById(R.id.textSum);
        cardPerson = (CardView)findViewById(R.id.cardPerson);
        checkUseCredit = (CheckBox)findViewById(R.id.checkUseCredit);
        checkUseCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkChanged();
            }
        });

        layoutToPay = (LinearLayout)findViewById(R.id.layoutToPay);
        layoutInclTip = (LinearLayout)findViewById(R.id.layoutInclTip);
        layoutGiven = (LinearLayout)findViewById(R.id.layoutGiven);
        layoutRetour = (LinearLayout)findViewById(R.id.layoutRetour);
        layoutRemainingCredit = (LinearLayout)findViewById(R.id.layoutRemainingCredit);

        textToPay = (EditText)findViewById(R.id.textToPay);
        textInclTip = (EditText)findViewById(R.id.textInclTip);
        textGiven = (EditText)findViewById(R.id.textGiven);
        textRetour = (EditText)findViewById(R.id.textRetour);
        textRemainingCredit = (EditText)findViewById(R.id.textRemainingCredit);

        textGiven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changedGiven();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textInclTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changedInclTip();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        avatarInclTipAdd = (TextView)findViewById(R.id.avatarInclTipAdd);
        avatarInclTipAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyInclTip(true);
            }
        });
        avatarInclTipRemove = (TextView)findViewById(R.id.avatarInclTipRemove);
        avatarInclTipRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyInclTip(false);
            }
        });
        avatarGivenAdd = (TextView)findViewById(R.id.avatarGivenAdd);
        avatarGivenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyGiven(ADDAMOUNT);
            }
        });
        avatarGivenRemove = (TextView)findViewById(R.id.avatarGivenRemove);
        avatarGivenRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyGiven(ADDAMOUNT*-1);
            }
        });

        buttonPay = findViewById(R.id.buttonPay);
        buttonPay.setOnClickListener(new View.OnClickListener() {
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
            sale = (Sale) Params.getParams(saleId);

            if(!sale.getPersonId().isEmpty()) {
                showProgress();
                listenerRegistration = Person.listenToId(sale.getPersonId(), context, new DbObj.OnLoadSingle() {
                    @Override
                    public void callback(Object obj) {
                        person = (Person) obj;
                        hideProgress();
                        loadUI();
                    }
                });
            }
            else
                loadUI();
        }
        else
            this.finish();
    }

    private void loadUI() {
        textCredit.setText("");
        checkUseCredit.setVisibility(View.GONE);
        hasCredit = false;
        creditBiggerThanSum = false;


        if(!sale.getPersonId().isEmpty()) {
            textName.setText(Person.getName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));
            textAvatar.setText(Person.getShortName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));

            if(person.getCredit() > 0) {
                hasCredit = true;
                String credit = getResources().getString(R.string.credit_short) + ": ";
                credit += Format.doubleToCurrency(person.getCredit());
                textCredit.setText(credit);
                checkUseCredit.setVisibility(View.VISIBLE);
                if(person.getCredit() >= sale.getSum())
                    creditBiggerThanSum = true;
            }
        }
        else {
            textName.setText(getResources().getString(R.string.person_direct));
            textAvatar.setText("D");
        }
        textDay.setText(sale.getDayStr());

        String sum = getResources().getString(R.string.sum)+" :" + Format.doubleToCurrency(sale.getSum());
        textSum.setText(sum);

        checkChanged();
        calc();
    }

    private void checkChanged() {
        boolean useCredit = checkUseCredit.isChecked();
        if(useCredit && creditBiggerThanSum) {
            layoutToPay.setVisibility(View.GONE);
            layoutInclTip.setVisibility(View.VISIBLE);
            layoutGiven.setVisibility(View.GONE);
            layoutRetour.setVisibility(View.GONE);
            layoutRemainingCredit.setVisibility(View.VISIBLE);
        }
        else {
            layoutToPay.setVisibility(View.VISIBLE);
            layoutInclTip.setVisibility(View.VISIBLE);
            layoutGiven.setVisibility(View.VISIBLE);
            layoutRetour.setVisibility(View.VISIBLE);
            layoutRemainingCredit.setVisibility(View.GONE);
        }
        calc();
    }

    private void calc() {
        calcToPay();
        estimate();

        textToPay.setText(Format.doubleToString(topay));
        textInclTip.setText(Format.doubleToString(incltip));
        textGiven.setText(Format.doubleToString(given));
        textRetour.setText(Format.doubleToString(retour));
        textRemainingCredit.setText(Format.doubleToString(remainingCredit));
    }

    private void calcToPay() {
        topay = sale.getSum();
        if(checkUseCredit.isChecked() && creditBiggerThanSum) {
            topay = 0;
            remainingCredit = person.getCredit() - sale.getSum();
        }
        else if(checkUseCredit.isChecked()) {
            topay -= person.getCredit();
            remainingCredit = 0;
        }
    }

    private void estimate() {
        incltip = 0;
        given = 0;
        retour = 0;
        if(topay > 0) {
            //incltip = Math.ceil(topay);
            incltip = topay;
            given = incltip;
            retour = 0;
        }
        else if(checkUseCredit.isChecked() && creditBiggerThanSum)
            incltip = sale.getSum();
    }

    private void modifyGiven(double amount) {
        given += amount;
        if(given < 0)
            given = 0;
        textGiven.setText(Format.doubleToString(given));
        changedGiven();
    }

    private void modifyInclTip(boolean plus) {
        if(plus) {
            if(incltip < Math.floor(incltip)+0.5)
                incltip = Math.floor(incltip)+0.5;
            else
                incltip = Math.ceil(incltip);
        }
        else {
            if(incltip > Math.floor(incltip)+0.5)
                incltip = Math.floor(incltip)+0.5;
            else if(incltip == Math.floor(incltip))
                incltip-=0.5;
            else
                incltip = Math.floor(incltip);
        }

        if(incltip < topay)
            incltip = topay;
        textInclTip.setText(Format.doubleToString(incltip));
        changedInclTip();
    }

    private void changedGiven() {
        try {
            given = Format.stringToDouble(textGiven.getText().toString());
            retour = given - incltip;
            textRetour.setText(Format.doubleToString(retour));
        }
        catch(Exception ex) {}
    }

    private void changedInclTip() {
        try {
            incltip = Format.stringToDouble(textInclTip.getText().toString());
            if(given < incltip)
                given = incltip;
            retour = given - incltip;

            if(checkUseCredit.isChecked() && creditBiggerThanSum) {
                remainingCredit = person.getCredit() - incltip;
                textRemainingCredit.setText(Format.doubleToString(remainingCredit));
            }

            textGiven.setText(Format.doubleToString(given));
            textRetour.setText(Format.doubleToString(retour));
        }
        catch(Exception ex) {}
    }


    private void save() {

        if(person!=null && checkUseCredit.isChecked()) {
            Double usedCredit = person.getCredit() - remainingCredit;
            sale.setUsedCredit(usedCredit);

            //modify persons credit
            person.addCredit(usedCredit*-1);
            person.save(this,null);
        }

        sale.setPayed(1);
        sale.setGiven(given);
        sale.setTip(incltip-sale.getSum());

        sale.save(this,null);

        double creditToAdd = 0.0;
        for(SaleArticle saleArticle : sale.getArticles()) {
            if(saleArticle.getIsCreditArticle() == 1)
                creditToAdd += saleArticle.getSumPrice();
        }

        if(creditToAdd > 0) {
            person.addCredit(creditToAdd);
            person.save(this,null);

            String msg = getResources().getText(R.string.added_credit).toString();
            msg = msg.replace("%pers", person.getName());
            msg = msg.replace("%eur", Format.doubleToCurrency(creditToAdd));
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }

        this.setResult(RESULT_OK);
        this.finish();
    }

    private Double ceil50(double val) {
        double floorVal = Math.floor(val);
        if(val >= floorVal+0.5)
            return Math.ceil(val);
        return floorVal+0.5;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
