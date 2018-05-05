package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.ArticlesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.SaleArticlesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.SaleArticle;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 25.02.2018.
 */

public class SaleActivity extends AuthedActivity {
    boolean newSale = true;
    boolean first = true;
    Sale sale;

    TextView textAvatar, textName, textSum, textDay, textCredit;
    Button buttonAddArticle;
    CardView cardPerson;
    FloatingActionButton fabOk, fabPay;

    private Context context;

    static final int CHOOSE_PERSON = 0;
    static final int CHOOSE_ARTICLE = 1;
    static final int PAY = 2;

    private SaleArticlesAdapter mAdapter;
    private BaseList mList;
    private Vector<SaleArticle> saleArticles = new Vector<>();
    private Vector<Sale> actsales = new Vector<>();

    ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        textAvatar = (TextView)findViewById(R.id.avatar);
        textName = (TextView)findViewById(R.id.textName);
        textSum = (TextView)findViewById(R.id.textSum);
        textDay = (TextView)findViewById(R.id.textDay);
        textCredit = (TextView)findViewById(R.id.textCredit);
        buttonAddArticle = (Button)findViewById(R.id.buttonAddArticle);
        fabOk = (FloatingActionButton)findViewById(R.id.buttonOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        fabPay = (FloatingActionButton)findViewById(R.id.buttonPay);
        fabPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });

        cardPerson = (CardView)findViewById(R.id.cardPerson);
        cardPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sale.getPayed() != 1)
                    choosePerson();
            }
        });
        buttonAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseArticle();
            }
        });

        String saleId = getIntent().getStringExtra("saleId");
        if(saleId == null)
            saleId = "";

        if(!saleId.isEmpty()) {
            newSale = false;
            sale = (Sale)Params.getParams(saleId);
            loadAdapter();
            loadUI();
            //showProgress();
            /*
            Sale.getById(saleId, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    hideProgress();
                    sale = (Sale)obj;
                    loadUI();
                }
            });*/
        }
        else {
            sale = Sale.newSale(null);

            String day = getIntent().getStringExtra("day");
            String sid = getIntent().getStringExtra("actSales");
            try {actsales = (Vector<Sale>)Params.getParams(sid);}
            catch(Exception ex) {}

            if(!day.isEmpty()) {
                Calendar cal = DF.StringToCalendar(day, "dd.MM.yyyy");
                sale.setDay(cal.getTime());
                sale.setDayStr(day);
            }

            loadAdapter();
            loadUI();
            choosePerson();
        }
    }

    private void loadAdapter() {
        mAdapter = new SaleArticlesAdapter(context, R.layout.listitem_sale_pos, sale, saleArticles, new SaleArticlesAdapter.IOnPosActionListener() {
            @Override
            public void onAdd(String articleId) {
                if(sale.getPayed() != 1) {
                    sale.addArticle(articleId, 1);
                    loadUI();
                }
            }

            @Override
            public void onRemove(String articleId) {
                if(sale.getPayed() != 1) {
                    sale.addArticle(articleId, -1);
                    loadUI();
                }
            }
        });
    }

    private void loadUI() {

        textCredit.setText("");

        if(sale.getPayed() == 1) {
            fabPay.setVisibility(View.GONE);
            fabOk.setVisibility(View.GONE);
            buttonAddArticle.setVisibility(View.GONE);
            textSum.setTextColor(getResources().getColor(R.color.colorDone));
        }

        if(!sale.getPersonId().isEmpty()) {
            getSupportActionBar().setTitle(newSale ? getResources().getString(R.string.new_sale) : Person.getName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()) );
            textName.setText(Person.getName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));
            textAvatar.setText(Person.getShortName(sale.getPersonLastName(), sale.getPersonFirstName(), sale.getPersonLinkName()));

            if(listenerRegistration != null)
                listenerRegistration.remove();

            listenerRegistration = Person.listenToId(sale.getPersonId(), context, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    Person person = (Person)obj;
                    if(person.getCredit() > 0) {
                        String credit = getResources().getString(R.string.credit_short) + ": ";
                        credit += Format.doubleToCurrency(person.getCredit());
                        textCredit.setText(credit);
                    }
                }
            });

        }
        else {
            getSupportActionBar().setTitle(newSale ? getResources().getString(R.string.new_sale) : getResources().getString(R.string.person_direct));
            textName.setText(getResources().getString(R.string.person_direct));
            textAvatar.setText("D");
        }

        String sum = getResources().getString(R.string.sum) + ": " + Format.doubleToCurrency(sale.getSum());
        textSum.setText(sum);

        textDay.setText(sale.getDayStr());

        if(mList == null)
            mList = new BaseList(context, R.id.swipeRefreshLayout, mAdapter);

        saleArticles.clear();
        for(SaleArticle saleArticle : sale.getArticles()) {
            saleArticles.add(saleArticle);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void save() {
        showProgress();
        sale.save(this,null);
        this.finish();
        /*sale.save(new DbObj.OnCallback() {
            @Override
            public void callback() {
                hideProgress();
                ((Activity)context).finish();
            }
        });*/
    }

    private void pay() {
        openPay();
        /*showProgress();
        sale.save(new DbObj.OnCallback() {
            @Override
            public void callback() {
                openPay();
                hideProgress();
            }
        });*/
    }

    private void openPay() {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("saleId", Params.setParams(sale));
        startActivityForResult(intent, PAY);
    }

    private void choosePerson() {
        Intent intent = new Intent(context, PersonChooserActivity.class);
        startActivityForResult(intent, CHOOSE_PERSON);
    }

    private void chooseArticle() {
        Intent intent = new Intent(context, ArticleChooserActivity.class);
        startActivityForResult(intent, CHOOSE_ARTICLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSE_PERSON && resultCode == RESULT_OK) {
            String personId = data.getStringExtra("personId");

            if(personId == null || personId.isEmpty()) { //directsale
                sale.setPersonId("");
                sale.setPersonLastName("");
                sale.setPersonFirstName("");
                sale.setPersonLinkName("");
                loadUI();

                if(newSale && first) {
                    first=false;
                    chooseArticle();
                }
            }
            else {

                Person person = (Person)Params.getParams(personId);
                boolean foundOpenSale = false;
                //check if there is already an opened sale for today for this person
                if(newSale && actsales != null && actsales.size() > 0) {
                    for(Sale s : actsales) {
                        if(s.getPersonId().equals(person.getId()) && s.getPayed() == 0) {
                            sale = s;
                            foundOpenSale = true;
                        }
                    }
                }

                if(!foundOpenSale) {
                    sale.setPersonId(person.getId());
                    sale.setPersonFirstName(person.getFirstName());
                    sale.setPersonLastName(person.getLastName());
                    sale.setPersonLinkName(person.getLinkName());
                }
                loadUI();

                if(newSale && first) {
                    first=false;
                    if(!foundOpenSale)
                        chooseArticle();
                }
                /*
                showProgress();
                Person.getById(personId, new DbObj.OnLoadSingle() {
                    @Override
                    public void callback(Object obj) {
                        hideProgress();
                        Person person = (Person)obj;
                        sale.setPerson(person.getReference());
                        sale.setPersonFirstName(person.getFirstName());
                        sale.setPersonLastName(person.getLastName());
                        loadUI();

                        if(newSale && first) {
                            first=false;
                            chooseArticle();
                        }
                    }
                });*/
            }
        }
        else if(requestCode == CHOOSE_ARTICLE && resultCode == RESULT_OK) {

            String paramsId = data.getStringExtra("paramsId");
            Object obj = Params.getParams(paramsId);
            if(obj != null) {
                Vector<AbstractMap.SimpleEntry<Article, Integer>> addedArticles = (Vector<AbstractMap.SimpleEntry<Article, Integer>>)obj;
                for(AbstractMap.SimpleEntry<Article, Integer> entry : addedArticles) {
                    int amount = entry.getValue();
                    Article article = entry.getKey();
                    sale.addArticle(article, amount);
                }
                loadUI();
            }
        }
        else if(requestCode == PAY && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void askDelete() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.delete_sale)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sale.delete(context);
                        ((Activity)context).finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!newSale)
            getMenuInflater().inflate(R.menu.toolbar_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                askDelete();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
