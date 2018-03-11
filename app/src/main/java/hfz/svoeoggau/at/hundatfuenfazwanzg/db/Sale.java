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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

public class Sale extends DbObj {

    public static final String COLLECTION = "sales";

    private Date day;
    private String dayStr;

    private Double sum = 0.0;
    private Double given = 0.0;
    private Double tip = 0.0;
    private Double usedCredit = 0.0;
    private Integer payed = 0;

    private String articlesText = "";
    private String personLastName = "";
    private String personFirstName = "";

    private List<SaleArticle> articles = new Vector<>();
    private String personId = "";
    private String user = "";
    private String mts = "";

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

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getPersonId() {
        return personId == null ? "" : personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public List<SaleArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SaleArticle> articles) {
        this.articles = articles;
    }

    public Integer getPayed() {
        return payed;
    }

    public void setPayed(Integer payed) {
        this.payed = payed;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    @Exclude
    public String getArticlesText() {
        return articlesText;
    }

    public void setArticlesText(String articlesText) {
        this.articlesText = articlesText;
    }

    public Double getGiven() {
        return given;
    }

    public void setGiven(Double given) {
        this.given = given;
    }

    public Double getTip() {
        return tip;
    }

    public void setTip(Double tip) {
        this.tip = tip;
    }

    public Double getUsedCredit() {
        return usedCredit;
    }

    public void setUsedCredit(Double usedCredit) {
        this.usedCredit = usedCredit;
    }

    public void save(Context context, final OnCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setUser(Security.getUser(context));
        setMts(DF.CalendarToString(DF.Now(), "dd.MM.yyyy HH:mm:ss"));

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(callback!=null)
                        callback.callback();
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
                    if(callback!=null)
                        callback.callback();
                }
            });
    }

    public void delete(final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String personId = getPersonId() != null ? getPersonId() : "";
        final double usedCredit = getUsedCredit();

        if(isIdSet()) {
            db.collection(COLLECTION).document(getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!personId.isEmpty() && usedCredit > 0) {
                        Person.getById(personId, new OnLoadSingle() {
                            @Override
                            public void callback(Object obj) {
                                Person person = (Person)obj;
                                person.addCredit(usedCredit);
                                person.save(context,null);
                            }
                        });
                    }
                }
            });
        }
    }

    public static Sale newSale(Person person) {
        Sale sale = new Sale();
        if(person != null) {
            sale.setPersonId(person.getId());
            sale.setPersonLastName(person.getLastName());
            sale.setPersonFirstName(person.getFirstName());
        }

        sale.setDay(new Date());
        sale.setDayStr(DF.CalendarToString(DF.Now()));
        return sale;
    }

    public void addArticle(Article article) {
        addArticle(article, 1);
    }

    public void addArticle(Article article, int amount) {
        boolean found = false;
        for(SaleArticle saleArticle : articles) {
            if(saleArticle.getArticleId().equals(article.getId())) {
                found=true;
                saleArticle.add(amount);
            }
        }

        if(!found) {
            SaleArticle saleArticle = SaleArticle.newSaleArticle(article);
            saleArticle.add(amount-1);
            articles.add(saleArticle);
        }

        calc();
    }

    public void addArticle(String articleId, int amount) {
        for(SaleArticle saleArticle : articles) {
            if(saleArticle.getArticleId().equals(articleId)) {
                saleArticle.add(amount);
            }
        }
        cleanUpArticles();
        calc();
    }

    private void cleanUpArticles() {
        for(int i = articles.size()-1; i >= 0; i--) {
            if(articles.get(i).getAmount() <= 0)
                articles.remove(i);
        }
    }

    public void calc() {
        double sum = 0.0;
        String articlesText = "";
        for(SaleArticle saleArticle : articles) {
            sum += saleArticle.getSumPrice();
            if(saleArticle.getAmount() > 1)
                articlesText += (int)saleArticle.getAmount() + "x ";
            articlesText += saleArticle.getArticleText()+", ";
        }
        if(articlesText.length() > 0)
            articlesText = articlesText.substring(0, articlesText.length()-2);

        setArticlesText(articlesText);
        setSum(sum);
    }

    public void markAsPayed(Double given, Double tip) {
        setPayed(1);
        setGiven(given);
        setTip(tip);
    }

    /*
    public static void getById(String id, final OnLoadSingle ols) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Sale sale = documentSnapshot.toObject(Sale.class);
                            sale.setReference(documentSnapshot.getReference());
                            ols.callback(sale);
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
    }*/

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getPersonLastName().toLowerCase().indexOf(search) >= 0 || getPersonFirstName().toLowerCase().indexOf(search) >= 0;
    }

    public static ListenerRegistration listen(final Vector<Sale> actList, String day,  final Context context, final OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListenerRegistration reg = db.collection(COLLECTION)
                .whereEqualTo("dayStr", day)
                .orderBy("payed")
                .orderBy(Person.LAST_NAME_FIRST ? "personLastName" : "personFirstName")
                .orderBy(Person.LAST_NAME_FIRST ? "personFirstName" : "personLastName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("HFZ", "listen:error", e);
                            if(context!= null){
                                try {
                                    Toast.makeText(context, R.string.failed_loading_sales, Toast.LENGTH_LONG).show();
                                }
                                catch(Exception ex) {}
                            }
                            return;
                        }

                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Sale sale = dc.getDocument().toObject(Sale.class);
                                sale.setId(dc.getDocument().getReference().getId());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(sale, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a person changes, it does not get reordered)
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, sale);

                                        try {
                                            if (newIndex != oldIndex && oldIndex == idx) {
                                                Sale s = actList.get(oldIndex);
                                                actList.remove(oldIndex);
                                                actList.add(newIndex, s);
                                            }
                                        }
                                        catch(Exception ex) {}

                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
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

    public static ListenerRegistration listenAll(final Vector<Sale> actList, final Context context, final OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListenerRegistration reg = db.collection(COLLECTION)
                .orderBy("day", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("HFZ", "listen:error", e);
                            if(context!= null){
                                try {
                                    Toast.makeText(context, R.string.failed_loading_sales, Toast.LENGTH_LONG).show();
                                }
                                catch(Exception ex) {}
                            }
                            return;
                        }

                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Sale sale = dc.getDocument().toObject(Sale.class);
                                sale.setId(dc.getDocument().getReference().getId());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(sale, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a person changes, it does not get reordered)
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, sale);

                                        try {
                                            if (newIndex != oldIndex && oldIndex == idx) {
                                                Sale s = actList.get(oldIndex);
                                                actList.remove(oldIndex);
                                                actList.add(newIndex, s);
                                            }
                                        }
                                        catch(Exception ex) {}

                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Sale p : actList) {
                                            if (p.getId().equals(sale.getId()))
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
