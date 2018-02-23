package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;

/**
 * Created by Christian on 23.02.2018.
 */

public class SettingsFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
