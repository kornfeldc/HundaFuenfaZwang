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
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;

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
    private Integer payed = 0;

    private String articlesText = "";
    private String personLastName = "";
    private String personFirstName = "";

    private List<SaleArticle> articles = new Vector<>();
    private DocumentReference person;

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

    public DocumentReference getPerson() {
        return person;
    }

    public void setPerson(DocumentReference person) {
        this.person = person;
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

    public void save(final OnCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(isIdSet())
            db.collection(COLLECTION).document(getId()).set(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(callback!=null)
                        callback.callback();
                }
            });
        else
            db.collection(COLLECTION).add(this).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    setReference(documentReference);
                    if(callback!=null)
                        callback.callback();
                }
            });
    }

    public void delete() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(isIdSet()) {
            db.collection(COLLECTION).document(getId()).delete();
        }
    }

    public static Sale newSale(Person person) {
        Sale sale = new Sale();
        if(person != null) {
            sale.setPerson(person.getReference());
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
            if(saleArticle.getArticle().getId().equals(article.getReference().getId())) {
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
            if(saleArticle.getArticle().getId().equals(articleId)) {
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

    public void loadSales(final OnLoadList oll) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo("isPayed", false)
                .orderBy("personName")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Object> list = new Vector<>();
                        if(!documentSnapshots.isEmpty()) {
                            for(DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                                Sale sale = documentSnapshot.toObject(Sale.class);
                                sale.setReference(documentSnapshot.getReference());
                                list.add(sale);
                            }
                        }
                        oll.callback(list);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("hfz", "Error loading sales", e);
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
    }

    public boolean matchSearch(String search) {
        search = search.toLowerCase();
        return search.equals("") || getPersonLastName().toLowerCase().indexOf(search) >= 0 || getPersonFirstName().toLowerCase().indexOf(search) >= 0;
    }

    public static void listen(final Vector<Sale> actList, final OnListChanged listChanged) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo("dayStr", DF.CalendarToString())
                .orderBy("payed")
                .orderBy(Person.LAST_NAME_FIRST ? "personLastName" : "personFirstName")
                .orderBy(Person.LAST_NAME_FIRST ? "personFirstName" : "personLastName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(documentSnapshots != null && documentSnapshots.getDocumentChanges() != null) {
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                Sale sale = dc.getDocument().toObject(Sale.class);
                                sale.setReference(dc.getDocument().getReference());

                                int newIndex = dc.getNewIndex();
                                int oldIndex = dc.getOldIndex();

                                int idx = -1;
                                int i = 0;

                                switch (dc.getType()) {
                                    case ADDED:
                                        //this can be called even if entry is already in list, so check if its not before adding
                                        boolean alreadyAdded = false;
                                        for (Sale p : actList) {
                                            if (p.getReference().getId().equals(sale.getReference().getId()))
                                                alreadyAdded = true;
                                        }
                                        if (!alreadyAdded)
                                            actList.insertElementAt(sale, newIndex);
                                        break;
                                    case MODIFIED:
                                        //index changes are not handled yet (e.g.: when the name of a person changes, it does not get reordered)
                                        for (Sale p : actList) {
                                            if (p.getReference().getId().equals(sale.getReference().getId()))
                                                idx = i;
                                            i++;
                                        }

                                        if (idx >= 0)
                                            actList.set(idx, sale);
                                        //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        for (Sale p : actList) {
                                            if (p.getReference().getId().equals(sale.getReference().getId()))
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
