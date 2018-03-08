package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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
    private Integer favorite = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    @Exclude
    public String getPriceStr() {
        return String.valueOf(price);
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

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
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

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getTitle().toLowerCase().indexOf(search) >= 0;
    }

    public static void listen(final Vector<Article> actList, final Article.OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .orderBy("favorite", Query.Direction.DESCENDING)
                .orderBy("category")
                .orderBy("title")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Article article = dc.getDocument().toObject(Article.class);
                                article.setReference(dc.getDocument().getReference());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Article a : actList) {
                                            if (a.getReference().getId().equals(article.getReference().getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(article, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a article changes, it does not get reordered)
                                        for (Article a : actList) {
                                            if (a.getReference().getId().equals(article.getReference().getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, article);
                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Article a : actList) {
                                            if (a.getReference().getId().equals(article.getReference().getId()))
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
    }

}
