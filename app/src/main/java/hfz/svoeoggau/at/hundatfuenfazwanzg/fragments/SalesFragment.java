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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Person;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.User;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.enums.Category;

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

        Person.search("Kornfeld", "Christian", new DbObj.OnLoadList() {
            @Override
            public void callback(List<Object> obj) {
                if(obj != null && obj.size() == 1) {
                    final Person person = (Person)obj.get(0);

                    Article.getAll(new DbObj.OnLoadList() {
                        @Override
                        public void callback(List<Object> obj) {
                            if(obj != null) {
                                List<DocumentReference> articles = new Vector<>();
                                for (Object o : obj) {
                                    articles.add(((Article)o).getReference());
                                }

                                Sale sale = new Sale();
                                sale.setDay(new Date());
                                sale.setPerson(person.getReference());
                                sale.setArticles(articles);
                                sale.save();
                            }
                        }
                    });


                }
            }
        });

    }
}
