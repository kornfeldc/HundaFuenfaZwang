package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class Person extends DbObj {

    public static final String COLLECTION = "persons";

    private String firstName="";
    private String lastName = "";


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this);
        else
            db.collection(COLLECTION).add(this).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    setReference(documentReference);
                }
            });
    }

    public static void getById(String id, final OnLoadSingle ols) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Person person = documentSnapshot.toObject(Person.class);
                            person.setReference(documentSnapshot.getReference());
                            ols.callback(person);
                        }
                        else
                            ols.callback(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ols.callback(null);
                    }
                });
    }

    public static void search(String lastName, String firstName, final OnLoadList oll) {
        //todo like name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("firstName", firstName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Person person = documentSnapshot.toObject(Person.class);
                                person.setReference(documentSnapshot.getReference());
                                list.add(person);
                            }
                        }
                        oll.callback(list);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        oll.callback(null);
                    }
                });
    }
}
