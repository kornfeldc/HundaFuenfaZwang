package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.ArticlesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.enums.Category;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 03.03.2018.
 */

public class ArticleChooserActivity extends BaseActivity {

    private ArticlesAdapter mAdapter;
    private BaseList mList;
    private Vector<Article> articles = new Vector<>();
    private Vector<Article> articlesFiltered = new Vector<>();
    private String search = "";
    private String category = "favorites";
    private Context context;
    private LinearLayout layoutAdded;
    private TextView textSelectedArticles, textDelete;
    private FloatingActionButton fab;

    private Vector<AbstractMap.SimpleEntry<Article, Integer>> addedArticles = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articlechooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;

        layoutAdded = (LinearLayout)findViewById(R.id.layoutAdded);
        textSelectedArticles = (TextView)findViewById(R.id.textSelectedArticles);
        textDelete = (TextView)findViewById(R.id.textDelete);
        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedArticles.clear();
                refreshAddedArticles();
            }
        });
        fab = (FloatingActionButton)findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectArticles();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite:
                        category = "favorites";
                        createFiltered();
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.alcoholic:
                        category = Category.ARR[0];
                        createFiltered();
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.nonalcoholic:
                        category = Category.ARR[1];
                        createFiltered();
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.meal:
                        category = Category.ARR[2];
                        createFiltered();
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.sweets:
                        category = Category.ARR[3];
                        createFiltered();
                        mAdapter.notifyDataSetChanged();
                        return true;
                }
                return false;
            }
        });

        mAdapter = new ArticlesAdapter(context, R.layout.listitem_article, articlesFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                addArticle(articlesFiltered.get(position));
            }
        });

        showProgress();
        Article.listen(articles, new Person.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createFiltered();
                if(mList == null)
                    mList = new BaseList(context, R.id.swipeRefreshLayout, mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void createFiltered() {
        articlesFiltered.clear();
        for(Article a : articles) {
            if(a.matchSearch(search)) {
                if((category == "favorites" && a.getFavorite() ==1) || a.getCategory().equals(category))
                    articlesFiltered.add(a);
            }
        }
    }

    private void addArticle(Article article) {
        boolean found = false;
        for(AbstractMap.SimpleEntry<Article, Integer> entry : addedArticles) {
            if(entry.getKey().getId().equals(article.getId())) {
                found=true;
                entry.setValue(entry.getValue() + 1);
            }
        }

        if(!found) {
            AbstractMap.SimpleEntry<Article, Integer> entry = new AbstractMap.SimpleEntry<Article, Integer>(article, 1);
            addedArticles.add(entry);
        }

        refreshAddedArticles();
    }

    private void refreshAddedArticles() {
        if(addedArticles.size() > 0) {
            fab.setVisibility(View.VISIBLE);
            layoutAdded.setVisibility(View.VISIBLE);
            String text = "";
            for(AbstractMap.SimpleEntry<Article, Integer> entry : addedArticles) {
                if(entry.getValue()>1)
                    text+=entry.getValue().toString()+"x ";
                text += entry.getKey().getTitle() + ", ";
            }
            if(text.length() > 0)
                text = text.substring(0, text.length()-2);
            textSelectedArticles.setText(text);
        }
        else
            layoutAdded.setVisibility(View.GONE);
    }

    private void selectArticles() {
        Intent ret = new Intent();
        Bundle bundle = new Bundle();

        String paramsId = Params.setParams(addedArticles);
        ret.putExtra("paramsId" , paramsId);

        setResult(RESULT_OK, ret);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                createFiltered();
                mAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search = newText;
                createFiltered();
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;

    }
}
