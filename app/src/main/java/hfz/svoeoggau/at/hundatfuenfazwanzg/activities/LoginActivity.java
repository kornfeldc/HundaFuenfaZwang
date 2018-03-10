package hfz.svoeoggau.at.hundatfuenfazwanzg.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import hfz.svoeoggau.at.hundatfuenfazwanzg.MainActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Security;

/**
 * Created by Christian on 10.03.2018.
 */

public class LoginActivity extends BaseActivity {
    EditText textUsername, textPassword;
    FloatingActionButton button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        textUsername = (EditText)findViewById(R.id.textUsername);
        textPassword = (EditText)findViewById(R.id.textPassword);
        button = (FloatingActionButton)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Security.auth(context, textUsername.getText().toString(), textPassword.getText().toString())) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    ((Activity)context).finish();
                }
                else {
                    Toast.makeText(context, R.string.auth_wrong, Toast.LENGTH_LONG).show();
                }
            }
        });

        if(Security.isAuthed(context, false)) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            ((Activity)context).finish();
        }
    }
}
