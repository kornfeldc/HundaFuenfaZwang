package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 25.02.2018.
 */

public class PersonActivity extends BaseActivity {
    boolean newPerson = true;
    Person person = new Person();

    EditText textLastName, textFirstName, textPhoneNr;
    CheckBox checkBoxMember;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textLastName = (EditText)findViewById(R.id.textLastName);
        textFirstName = (EditText)findViewById(R.id.textFirstName);
        textPhoneNr = (EditText)findViewById(R.id.textPhoneNr);
        checkBoxMember = (CheckBox)findViewById(R.id.checkBoxMember);
        fab = (FloatingActionButton)findViewById(R.id.button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        String personId = getIntent().getStringExtra("personId");
        if(personId == null)
            personId = "";

        if(!personId.isEmpty()) {
            newPerson = false;
            Person.getById(personId, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    person = (Person)obj;
                    loadUI();
                }
            });
        }
        else
            loadUI();
    }

    private void loadUI() {

        getSupportActionBar().setTitle(newPerson ? getResources().getString(R.string.new_person) : person.getName());

        textLastName.setText(person.getLastName());
        textFirstName.setText(person.getFirstName());
        textPhoneNr.setText(person.getPhoneNr());
        checkBoxMember.setChecked(person.getMember() != 0);
    }

    private void save() {
        person.setLastName(textLastName.getText().toString());
        person.setFirstName(textFirstName.getText().toString());

        if(person.getFirstName().isEmpty() && person.getLastName().isEmpty())
            Toast.makeText(this, getResources().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
        else {
            person.setPhoneNr(textPhoneNr.getText().toString());
            person.setMember(checkBoxMember.isChecked() ? 1 : 0);
            person.save();
            this.finish();
        }
    }

    private void askDelete() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.delete_person)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        person.delete();
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
        if(!newPerson)
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
