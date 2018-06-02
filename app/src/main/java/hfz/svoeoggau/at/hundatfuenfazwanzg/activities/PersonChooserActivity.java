package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 03.03.2018.
 */

public class PersonChooserActivity extends AuthedActivity {

    private PersonsAdapter mAdapter;
    private BaseList mList;
    private Vector<Person> persons = new Vector<>();
    private Vector<Person> personsFiltered = new Vector<>();
    //private FloatingActionButton fab;
    private Button buttonNewPerson;
    private String search = "";
    private Context context;
    private ListenerRegistration listenerRegistration;
    static final int CHOOSE_PERSON = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personchooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;

        buttonNewPerson = findViewById(R.id.buttonNewPerson);
        buttonNewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPerson();
            }
        });

        mAdapter = new PersonsAdapter(context, R.layout.listitem_person, personsFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                selectPerson(personsFiltered.get(position));
            }
        });

        showProgress();
        listenerRegistration = Person.listen(persons, context, new Person.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createFiltered();
                if(mList == null) {
                    mList = new BaseList(context, R.id.swipeRefreshLayout, mAdapter);
                    //mList.hideFabOnScroll(fab);
                }
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

        //check if person is linked to a master
        if(!person.getLinkedPersonId().isEmpty() && person.getLinkMaster() == 0) {
            showProgress();
            Person.getById(person.getLinkedPersonId(), new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    hideProgress();
                    if(obj != null)
                        selectPerson((Person) obj);
                }
            });
        }
        else {
            Intent ret = new Intent();
            ret.putExtra("personId", person.isDirect ? "" : Params.setParams(person));
            setResult(RESULT_OK, ret);
            finish();
        }
    }

    private void newPerson() {
        Person person = new Person();
        person.setFirstName(Format.capitalize(search));

        Intent intent = new Intent(context, PersonActivity.class);
        intent.putExtra("personId", Params.setParams(person));
        startActivityForResult(intent, CHOOSE_PERSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_PERSON && resultCode == RESULT_OK) {
            Person person = (Person)Params.getParams(data.getStringExtra("personId"));
            selectPerson(person);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration != null)
            listenerRegistration.remove();
    }
}
