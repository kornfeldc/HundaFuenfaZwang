package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.enums.Category;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;

/**
 * Created by Christian on 25.02.2018.
 */

public class ArticleActivity extends BaseActivity {

    boolean newArticle = true;
    Article article = new Article();

    EditText textTitle, textPrice;
    CheckBox checkBoxFavorite;
    Spinner spinnerCategory;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textTitle = (EditText)findViewById(R.id.textTitle);
        textPrice = (EditText)findViewById(R.id.textPrice);
        checkBoxFavorite = (CheckBox)findViewById(R.id.checkBoxFavorite);
        spinnerCategory = (Spinner)findViewById(R.id.spinnerCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.article_category,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        fab = (FloatingActionButton)findViewById(R.id.button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        String articleId = getIntent().getStringExtra("articleId");
        if(articleId == null)
            articleId = "";

        if(!articleId.isEmpty()) {
            newArticle = false;
            showProgress();
            Article.getById(articleId, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    hideProgress();
                    article = (Article)obj;
                    loadUI();
                }
            });
        }
        else
            loadUI();
    }

    private void loadUI() {

        getSupportActionBar().setTitle(newArticle ? getResources().getString(R.string.new_article) : article.getTitle());

        textTitle.setText(article.getTitle());
        textPrice.setText(Format.doubleToString(article.getPrice()));
        checkBoxFavorite.setChecked(article.getFavorite() != 0);
        spinnerCategory.setSelection(Category.getIndex(article.getCategory()), true);
    }

    private void save() {

        int index = spinnerCategory.getSelectedItemPosition();

        if(textTitle.getText().toString().isEmpty() || textPrice.getText().toString().isEmpty() || index < 0) {
            Toast.makeText(this, getResources().getString(R.string.enter_fields), Toast.LENGTH_LONG).show();
        }
        else {
            article.setTitle(textTitle.getText().toString());
            article.setPrice(Format.stringToDouble(textPrice.getText().toString()));
            article.setFavorite(checkBoxFavorite.isChecked() ? 1 : 0);
            article.setCategory(Category.ARR[index]);
            article.save();
            this.finish();
        }
    }

    private void askDelete() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.delete_article)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        article.delete();
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
        if(!newArticle)
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
