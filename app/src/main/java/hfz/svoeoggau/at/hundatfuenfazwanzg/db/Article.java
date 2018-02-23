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

public class Article extends DbObj {

    public static final String COLLECTION = "articles";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_CATEGORY = "category";

    private String id = "";
    private String title="";
    private double price = 0.0;
    private String category = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public void parseMap(String id, Map<String,Object> map) {
        setId(id);
        setTitle(map.get(FIELD_TITLE).toString());
        setCategory(map.get(FIELD_CATEGORY).toString());
        setPrice(Double.parseDouble(map.get(FIELD_PRICE).toString()));
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_TITLE, getTitle());
        map.put(FIELD_PRICE, getPrice());
        map.put(FIELD_CATEGORY, getCategory());
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
                            Article article = new Article();
                            article.parseMap(documentSnapshot.getId(), documentSnapshot.getData());
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

    public static void getByCategory(String category, final OnLoadList oll) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo(FIELD_CATEGORY, category)
                .orderBy(FIELD_CATEGORY)
                .orderBy(FIELD_TITLE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Article article = new Article();
                                article.parseMap(documentSnapshot.getId(), documentSnapshot.getData());
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
