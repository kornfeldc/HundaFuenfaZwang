package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by Christian on 25.02.2018.
 */

public class SaleArticle {
    DocumentReference article;

    String articleText = "";
    double singlePrice;
    double sumPrice;
    double amount;

    public DocumentReference getArticle() {
        return article;
    }

    public void setArticle(DocumentReference article) {
        this.article = article;
    }

    public double getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(double singlePrice) {
        this.singlePrice = singlePrice;
    }

    public double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addOne() {
        amount++;
        sumPrice+=singlePrice;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public static SaleArticle newSaleArticle(Article article) {
        SaleArticle saleArticle = new SaleArticle();
        saleArticle.setArticle(article.getReference());
        saleArticle.setSinglePrice(article.getPrice());
        saleArticle.setAmount(1);
        saleArticle.setSumPrice(article.getPrice());
        saleArticle.setArticleText(article.getTitle());
        return saleArticle;
    }
}
