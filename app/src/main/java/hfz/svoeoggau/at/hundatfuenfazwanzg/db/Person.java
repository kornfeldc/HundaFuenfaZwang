package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.classes.CreditHistory;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Security;

/**
 * Created by Christian on 23.02.2018.
 */

public class Person extends DbObj {

    public static final String COLLECTION = "persons";
    public static final Boolean LAST_NAME_FIRST = false;

    private String firstName="";
    private String lastName = "";
    private Integer member = 0;
    private String memberNr = "";
    private String phoneNr  ="";
    private Double credit = 0.0;
    private List<CreditHistory> creditHistory = new Vector<CreditHistory>();
    private String user = "";
    private String mts = "";

    private String linkedPersonId = "";
    private Integer linkMaster = 0;
    private String linkName = "";

    public String getMts() {
        return mts;
    }

    public void setMts(String mts) {
        this.mts = mts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Exclude
    public boolean isDirect=false;

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
        return Person.getName(lastName, firstName);
    }

    @Exclude
    public String getShortName() {
        return Person.getShortName(lastName, firstName);
    }

    @Exclude
    public static String getShortName(String lastName, String firstName) {
        return getShortName(lastName, firstName, "");
    }

    @Exclude
    public static String getShortName(String lastName, String firstName, String linkName) {
        String ret = "";

        if(!linkName.equals("")) {
            String[] split = linkName.split(" ");
            for(String s : split) {
                String sf = s.substring(0,1);
                if("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(sf))
                    ret+=sf;
            }
        }
        else {
            if (LAST_NAME_FIRST && !lastName.isEmpty())
                ret += lastName.substring(0, 1);
            if (!firstName.isEmpty())
                ret += firstName.substring(0, 1);
            if (!LAST_NAME_FIRST && !lastName.isEmpty())
                ret += lastName.substring(0, 1);
        }
        return ret.toUpperCase();
    }

    @Exclude
    public static String getName(String lastName, String firstName) {
        return getName(lastName, firstName, "");
    }

    @Exclude
    public static String getName(String lastName, String firstName, String linkName) {

        if(!linkName.isEmpty())
            return linkName;
        else if(!firstName.isEmpty() && !lastName.isEmpty()) {
            if(LAST_NAME_FIRST)
                return lastName + " " + firstName;
            else
                return firstName + " " + lastName;
        }
        else if(!firstName.isEmpty())
            return firstName;
        return lastName;
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

    public String getLinkedPersonId() {
        return linkedPersonId;
    }

    public void setLinkedPersonId(String linkedPersonId) {
        this.linkedPersonId = linkedPersonId;
    }

    public Integer getLinkMaster() {
        return linkMaster;
    }

    public void setLinkMaster(Integer linkMaster) {
        this.linkMaster = linkMaster;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public List<CreditHistory> getCreditHistory() {
        return creditHistory;
    }

    public void setCreditHistory(List<CreditHistory> creditHistory) {
        this.creditHistory = creditHistory;
    }

    public void addCredit(double credit) {
        this.creditHistory.add(new CreditHistory(credit));
        this.credit += credit;
    }

    public void save(Context context, final OnCallback onCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setUser(Security.getUser(context));
        setMts(DF.CalendarToString(DF.Now(), "dd.MM.yyyy HH:mm:ss"));

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(onCallback != null)
                        onCallback.callback();
                }
            });
        else
            db.collection(COLLECTION).add(this).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {


                }
            }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    setId(documentReference.getId());
                    if(onCallback != null)
                        onCallback.callback();
                }
            });
    }

    public void delete() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(isIdSet()) {
            db.collection(COLLECTION).document(getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
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
                            person.setId(documentSnapshot.getReference().getId());
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

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getLastName().toLowerCase().indexOf(search) >= 0 || getFirstName().toLowerCase().indexOf(search) >= 0;
    }

    public static ListenerRegistration listenToId(String id, final Context context, final OnLoadSingle loadSingle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION)
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("HFZ", "listen:error", e);
                            if(context!= null){
                                try {
                                    Toast.makeText(context, R.string.failed_loading_persons, Toast.LENGTH_LONG).show();
                                }
                                catch(Exception ex) {}
                            }
                            return;
                        }

                        if(documentSnapshot != null && loadSingle != null) {
                            Person person = documentSnapshot.toObject(Person.class);
                            person.setId(documentSnapshot.getReference().getId());
                            loadSingle.callback(person);
                        }
                    }
                });
    }

    public static ListenerRegistration listen(final Vector<Person> actList, final Context context, final OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListenerRegistration reg = db.collection(COLLECTION)
                .orderBy("member", Query.Direction.DESCENDING)
                .orderBy(LAST_NAME_FIRST ? "lastName" : "firstName")
                .orderBy(LAST_NAME_FIRST ? "firstName" : "lastName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("HFZ", "listen:error", e);
                            if(context!= null){
                                try {
                                    Toast.makeText(context, R.string.failed_loading_persons, Toast.LENGTH_LONG).show();
                                }
                                catch(Exception ex) {}
                            }
                            return;
                        }

                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Person person = dc.getDocument().toObject(Person.class);
                                person.setId(dc.getDocument().getReference().getId());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Person p : actList) {
                                            if (p.getId().equals(person.getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(person, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a person changes, it does not get reordered)
                                        for (Person p : actList) {
                                            if (p.getId().equals(person.getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, person);
                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Person p : actList) {
                                            if (p.getId().equals(person.getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.remove(idx);
                                        //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                        break;
                                }
                            }
                        }
                        listChanged.callback();
                    }
                });
        return reg;
    }
}
