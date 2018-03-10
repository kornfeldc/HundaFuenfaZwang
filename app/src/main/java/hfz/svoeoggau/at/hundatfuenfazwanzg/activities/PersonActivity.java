package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Format;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 25.02.2018.
 */

public class PersonActivity extends AuthedActivity {
    boolean newPerson = true;
    Person person = new Person();

    EditText textLastName, textFirstName, textPhoneNr;
    TextView textCredit;
    CheckBox checkBoxMember;
    Button buttonAddCredit;
    FloatingActionButton fab;
    ImageView imageSwap;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        context =  this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textLastName = (EditText)findViewById(R.id.textLastName);
        textFirstName = (EditText)findViewById(R.id.textFirstName);
        textPhoneNr = (EditText)findViewById(R.id.textPhoneNr);
        checkBoxMember = (CheckBox)findViewById(R.id.checkBoxMember);
        textCredit = (TextView)findViewById(R.id.textCredit);
        buttonAddCredit = (Button)findViewById(R.id.buttonAddCredit);
        imageSwap = (ImageView)findViewById(R.id.imageSwap);
        fab = (FloatingActionButton)findViewById(R.id.button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        buttonAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open input dialog with single number
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.add_credit);
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText("20");
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here
                        String creditStr = input.getText().toString();
                        if(creditStr != null && !creditStr.isEmpty()) {
                            double addCredit = Double.valueOf(creditStr);
                            person.addCredit(addCredit);
                            loadCredit();
                        }
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alert.show();
            }
        });

        imageSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lastName = textLastName.getText().toString();
                textLastName.setText(textFirstName.getText());
                textFirstName.setText(lastName);
            }
        });

        String personId = getIntent().getStringExtra("personId");
        if(personId == null)
            personId = "";

        if(!personId.isEmpty()) {
            newPerson = false;
            person = (Person) Params.getParams(personId);
            loadUI();
            /*showProgress();
            Person.getById(personId, new DbObj.OnLoadSingle() {
                @Override
                public void callback(Object obj) {
                    hideProgress();
                    person = (Person)obj;
                    loadUI();
                }
            });*/
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
        loadCredit();
    }

    private void loadCredit() {
        String s = getResources().getString(R.string.credit);
        s+=": "+ Format.doubleToCurrency(person.getCredit());
        textCredit.setText(s);
    }

    private void save() {
        person.setLastName(textLastName.getText().toString());
        person.setFirstName(textFirstName.getText().toString());

        if(person.getFirstName().isEmpty() && person.getLastName().isEmpty())
            Toast.makeText(this, getResources().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
        else {
            person.setPhoneNr(textPhoneNr.getText().toString());
            person.setMember(checkBoxMember.isChecked() ? 1 : 0);
            person.save(this, new DbObj.OnCallback() {
                @Override
                public void callback() {
                    Intent ret = new Intent();
                    ret.putExtra("personId", Params.setParams(person));
                    ((Activity)context).setResult(RESULT_OK, ret);
                    ((Activity)context).finish();
                }
            });


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
