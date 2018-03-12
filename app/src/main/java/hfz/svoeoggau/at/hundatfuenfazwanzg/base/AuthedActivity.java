package hfz.svoeoggau.at.hundatfuenfazwanzg.base;

import android.content.Intent;
import android.os.Bundle;

import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.LoginActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Security;

/**
 * Created by Christian on 25.02.2018.
 */

public class AuthedActivity extends BaseActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Security.isAuthed(this, true)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
