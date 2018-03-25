package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Security;

/**
 * Created by Christian on 23.02.2018.
 */

public class Article extends DbObj {

    public static final String COLLECTION = "articles";

    private String title="";
    private double price = 0.0;
    private String category = "";
    private Integer favorite = 0;
    private String user = "";
    private String mts = "";
    private Integer isCreditArticle = 0;
    private Integer isShoppingArticle = 0;

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

    public Integer getIsCreditArticle() {
        return isCreditArticle;
    }

    public void setIsCreditArticle(Integer isCreditArticle) {
        this.isCreditArticle = isCreditArticle;
    }

    public Integer getIsShoppingArticle() {
        return isShoppingArticle;
    }

    public void setIsShoppingArticle(Integer isShoppingArticle) {
        this.isShoppingArticle = isShoppingArticle;
    }

    public void save(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setUser(Security.getUser(context));
        setMts(DF.CalendarToString(DF.Now(), "dd.MM.yyyy HH:mm:ss"));

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        else
            db.collection(COLLECTION).add(this)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        setId(documentReference.getId());
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
            db.collection(COLLECTION).document(getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    /*public static void getById(String id, final OnLoadSingle ols) {
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
    }*/

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getTitle().toLowerCase().indexOf(search) >= 0;
    }

    public static ListenerRegistration listen(final Vector<Article> actList, final Context context, final Article.OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListenerRegistration reg = db.collection(COLLECTION)
                .orderBy("favorite", Query.Direction.DESCENDING)
                .orderBy("category")
                .orderBy("title")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("HFZ", "listen:error", e);
                            if(context!= null){
                                try {
                                    Toast.makeText(context, R.string.failed_loading_articles, Toast.LENGTH_LONG).show();
                                }
                                catch(Exception ex) {}
                            }
                            return;
                        }

                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Article article = dc.getDocument().toObject(Article.class);
                                article.setId(dc.getDocument().getReference().getId());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Article a : actList) {
                                            if (a.getId().equals(article.getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(article, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a article changes, it does not get reordered)
                                        for (Article a : actList) {
                                            if (a.getId().equals(article.getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, article);
                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Article a : actList) {
                                            if (a.getId().equals(article.getId()))
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
