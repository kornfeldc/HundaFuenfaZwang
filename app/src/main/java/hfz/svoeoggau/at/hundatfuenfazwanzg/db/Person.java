package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class Person extends DbObj {

    public static final String COLLECTION = "articles";
    public static final String FIELD_FIRSTNAME = "firstName";
    public static final String FIELD_LASTNAME = "lastName";

    private String id = "";
    private String firstName="";
    private String lastName = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public void parseMap(String id, Map<String,Object> map) {
        setId(id);
        setFirstName(map.get(FIELD_FIRSTNAME).toString());
        setLastName(map.get(FIELD_LASTNAME).toString());
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_FIRSTNAME, getFirstName());
        map.put(FIELD_LASTNAME, getLastName());
        return map;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(id != "")
            db.collection(COLLECTION).document(id).set(getMap());
        else
            db.collection(COLLECTION).add(getMap());
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
                            Person person = new Person();
                            person.parseMap(documentSnapshot.getId(), documentSnapshot.getData());
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

    public static void search(String name, final OnLoadList oll) {
        //todo like name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .orderBy(FIELD_LASTNAME)
                .orderBy(FIELD_FIRSTNAME)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Person person = new Person();
                                person.parseMap(documentSnapshot.getId(), documentSnapshot.getData());
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
