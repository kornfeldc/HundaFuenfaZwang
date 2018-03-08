package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;

/**
 * Created by Christian on 03.03.2018.
 */

public class PersonChooserActivity extends BaseActivity {

    private PersonsAdapter mAdapter;
    private BaseList mList;
    private Vector<Person> persons = new Vector<>();
    private Vector<Person> personsFiltered = new Vector<>();
    private String search = "";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personchooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;

        mAdapter = new PersonsAdapter(context, R.layout.listitem_person, personsFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                selectPerson(personsFiltered.get(position));
            }
        });

        showProgress();
        Person.listen(persons, new Person.OnListChanged() {
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
        personsFiltered.clear();

        if(search == "") {
            //add direct sale if no search term is entered
            Person p = new Person();
            p.setLastName(getResources().getString(R.string.person_direct));
            p.isDirect = true;
            personsFiltered.add(p);
        }

        for(Person p : persons) {
            if(p.matchSearch(search))
                personsFiltered.add(p);
        }
    }

    private void selectPerson(Person person) {
        Intent ret = new Intent();
        ret.putExtra("personId", person.isDirect ? "" : person.getReference().getId());
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
