package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class Sale extends DbObj {

    public static final String COLLECTION = "sales";
    public static final String ARTICLES_COLLECTION = "articles";

    private Date day;

    private Double sum;
    private List<DocumentReference> articles;
    private DocumentReference person;

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public DocumentReference getPerson() {
        return person;
    }

    public void setPerson(DocumentReference person) {
        this.person = person;
    }

    public List<DocumentReference> getArticles() {
        return articles;
    }

    public void setArticles(List<DocumentReference> articles) {
        this.articles = articles;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(isIdSet()) {
            db.collection(COLLECTION)
                    .document(getId())
                    .set(this, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else
            db.collection(COLLECTION).add(this)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        setReference(documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String s = e.getMessage();
                    }
                });
    }
}
