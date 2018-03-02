package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private Integer member = 0;
    private String memberNr = "";
    private String phoneNr  ="";
    private Double credit = 0.0;

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

    @Exclude
    public String getName() {
        if(!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        else if(!firstName.isEmpty())
            return firstName;
        return lastName;
    }

    @Exclude
    public String getShortName() {
        String ret = "";
        if(!lastName.isEmpty())
            ret+=lastName.substring(0,1);
        if(!firstName.isEmpty())
            ret += firstName.substring(0,1);
        return ret.toUpperCase();
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    public String getMemberNr() {
        return memberNr;
    }

    public void setMemberNr(String memberNr) {
        this.memberNr = memberNr;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
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

    public void delete() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(isIdSet()) {
            db.collection(COLLECTION).document(getId()).delete();
        }
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

    public static void load(String search, final OnLoadList oll) {
        //todo like name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .orderBy("lastName")
                .orderBy("firstName")
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

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getLastName().toLowerCase().indexOf(search) >= 0 || getFirstName().toLowerCase().indexOf(search) >= 0;
    }

    public static void listen(final Vector<Person> actList, final OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .orderBy("member", Query.Direction.DESCENDING)
                .orderBy("firstName")
                .orderBy("lastName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Person person = dc.getDocument().toObject(Person.class);
                                person.setReference(dc.getDocument().getReference());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Person p : actList) {
                                            if (p.getReference().getId().equals(person.getReference().getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(person, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a person changes, it does not get reordered)
                                        for (Person p : actList) {
                                            if (p.getReference().getId().equals(person.getReference().getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, person);
                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Person p : actList) {
                                            if (p.getReference().getId().equals(person.getReference().getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.remove(idx);
                                        //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                        break;
                                }
                            }
                            listChanged.callback();
                        }
                    }
                });
    }
}
