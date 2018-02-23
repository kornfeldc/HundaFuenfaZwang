package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.User;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class SalesFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        User.getByLogin("ck", new DbObj.OnLoadSingle() {
            @Override
            public void callback(Object obj) {
                if(obj != null) {
                    User user = (User) obj;
                }
            }
        });
    }
}
