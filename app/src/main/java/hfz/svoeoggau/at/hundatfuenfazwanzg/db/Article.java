package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class Article extends DbObj {

    public static final String COLLECTION = "articles";

    private String title="";
    private double price = 0.0;
    private String category = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this, SetOptions.merge());
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

    public static void getById(String id, final OnLoadSingle ols) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Article article = documentSnapshot.toObject(Article.class);
                            article.setReference(documentSnapshot.getReference());
                            ols.callback(article);
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

    public static void getAll(final OnLoadList oll) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .orderBy("category")
                .orderBy("title")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Article article = documentSnapshot.toObject(Article.class);
                                article.setReference(documentSnapshot.getReference());
                                list.add(article);
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

    public static void getByCategory(String category, final OnLoadList oll) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo("category", category)
                .orderBy("title")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Article article = documentSnapshot.toObject(Article.class);
                                article.setReference(documentSnapshot.getReference());
                                list.add(article);
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
