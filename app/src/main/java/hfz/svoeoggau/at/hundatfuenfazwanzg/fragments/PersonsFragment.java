package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class PersonsFragment extends BaseFragment {

    private PersonsAdapter mAdapter;
    private BaseList mList;
    private Vector<Person> persons = new Vector<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_persons, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter = new PersonsAdapter(getActivity(), R.layout.listitem_person, persons, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(ViewGroup viewGroup, View view, int position, T item) {
            }
        });

        mList = new BaseList(getActivity(), R.id.swipeRefreshLayout, mAdapter);

        Person.listen(persons, new Person.OnListChanged() {
            @Override
            public void callback() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
