package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;

/**
 * Created by Christian on 04.03.2018.
 */

public class ProgressBarHandler {

    public static void showProgress(View parent) {
        ProgressBar progressBar = (ProgressBar)parent.findViewById(R.id.progressBar);
        TextView pleaseWait = (TextView)parent.findViewById(R.id.pleasewait);
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            setAllVisibility(parent, View.INVISIBLE);
            pleaseWait.setVisibility(View.VISIBLE);
        }
    }

    public static void hideProgress(View parent) {
        if(parent != null) { //can be null if view gets destroyed in the meantime
            ProgressBar progressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
            TextView pleaseWait = (TextView) parent.findViewById(R.id.pleasewait);
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
                setAllVisibility(parent, View.VISIBLE);
                pleaseWait.setVisibility(View.GONE);
            }
        }
    }

    private static void setAllVisibility(View parent, int visibility) {

        ViewGroup vg = parent.findViewById(R.id.coordinator);
        if(vg != null) {
            int count = vg.getChildCount();
            for(int i = 0; i < count; i++) {
                View v = vg.getChildAt(i);
                int id = v.getId();
                if(id != R.id.progressBar && id != R.id.progressBarRoot)
                    v.setVisibility(visibility);
            }
        }
    }

}
