package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.PersonActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.PersonsAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 23.02.2018.
 */

public class PersonsFragment extends BaseFragment {

    private PersonsAdapter mAdapter;
    private BaseList mList;
    private Vector<Person> persons = new Vector<>();
    private Vector<Person> personsFiltered = new Vector<>();
    //private FloatingActionButton fab;
    private Button buttonNewPerson;
    private String search = "";
    private ListenerRegistration listenerRegistration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_persons, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonNewPerson = getView().findViewById(R.id.buttonNewPerson);
        buttonNewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPerson(null);
            }
        });

        mAdapter = new PersonsAdapter(getActivity(), R.layout.listitem_person, personsFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                openPerson(personsFiltered.get(position));
            }
        });

        setOnSearchListener(new OnSearch() {
            @Override
            public void search(String searchStr) {
                search = searchStr;
                createFiltered();
                mAdapter.notifyDataSetChanged();
            }
        });

        showProgress();
        listenerRegistration = Person.listen(persons, getContext(), new Person.OnListChanged() {
            @Override
            public void callback() {
                hideProgress();
                createFiltered();
                if(mList == null) {
                    mList = new BaseList(getActivity(), R.id.swipeRefreshLayout, mAdapter);
                    //mList.hideFabOnScroll(fab);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void createFiltered() {
        personsFiltered.clear();
        for(Person p : persons) {
            if(p.matchSearch(search))
                personsFiltered.add(p);
        }
    }

    private void openPerson(Person person) {
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        if(person != null) {
            //intent.putExtra("personId", person.getReference().getId());
            intent.putExtra("personId", Params.setParams(person));
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(listenerRegistration != null)
            listenerRegistration.remove();
    }

}
