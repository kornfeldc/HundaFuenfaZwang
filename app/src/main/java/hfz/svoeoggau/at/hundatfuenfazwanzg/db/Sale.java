package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;
import android.util.Log;

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

    private Date day;

    private Double sum = 0.0;
    private Double given = 0.0;
    private Double tip = 0.0;
    private Date payedDate;
    private boolean isPayed = false;
    private String articlesText = "";
    private String personName = "";

    private List<SaleArticle> articles = new Vector<>();
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

    public List<SaleArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SaleArticle> articles) {
        this.articles = articles;
    }

    public Date getPayedDate() {
        return payedDate;
    }

    public void setPayedDate(Date payedDate) {
        this.payedDate = payedDate;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        this.isPayed = payed;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

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

    public static Sale newSale(Person person) {
        Sale sale = new Sale();
        if(person != null) {
            sale.setPerson(person.getReference());
            sale.setPersonName(person.getName());
        }
        sale.setDay(new Date());
        return sale;
    }

    public void addArticle(Article article) {
        boolean found = false;
        for(SaleArticle saleArticle : articles) {
            if(saleArticle.getArticle().getId().equals(article.getReference().getId())) {
                found=true;
                saleArticle.addOne();
            }
        }

        if(!found) {
            SaleArticle saleArticle = SaleArticle.newSaleArticle(article);
            articles.add(saleArticle);
        }

        calc();
    }

    private void calc() {
        double sum = 0.0;
        String articlesText = "";
        for(SaleArticle saleArticle : articles) {
            sum += saleArticle.getSumPrice();
            if(saleArticle.getAmount() > 1)
                articlesText += saleArticle.getAmount() + "x ";
            articlesText += saleArticle.getArticleText()+", ";
        }
        if(articlesText.length() > 0)
            articlesText = articlesText.substring(0, articlesText.length()-2);

        setArticlesText(articlesText);
        setSum(sum);
    }

    public void markAsPayed(Double given, Double tip) {
        setPayed(true);
        setPayedDate(new Date());
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


}
