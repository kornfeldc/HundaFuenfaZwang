package hfz.svoeoggau.at.hundatfuenfazwanzg.base;

import android.support.v7.app.AppCompatActivity;

import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.ProgressBarHandler;

/**
 * Created by Christian on 25.02.2018.
 */

public class BaseActivity extends AppCompatActivity {
    public void showProgress() {
        ProgressBarHandler.showProgress(getWindow().getDecorView().getRootView());
    }

    public void hideProgress() {
        ProgressBarHandler.hideProgress(getWindow().getDecorView().getRootView());
    }
}
