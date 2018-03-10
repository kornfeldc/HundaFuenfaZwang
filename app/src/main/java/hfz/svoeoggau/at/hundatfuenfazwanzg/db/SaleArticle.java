package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by Christian on 25.02.2018.
 */

public class SaleArticle {
    String articleId;

    String articleText = "";
    double singlePrice;
    double sumPrice;
    double amount;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
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

    public void add(int pamount) {
        this.amount+=pamount;
        sumPrice+=(pamount*singlePrice);
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public static SaleArticle newSaleArticle(Article article) {
        SaleArticle saleArticle = new SaleArticle();
        saleArticle.setArticleId(article.getId());
        saleArticle.setSinglePrice(article.getPrice());
        saleArticle.setAmount(1);
        saleArticle.setSumPrice(article.getPrice());
        saleArticle.setArticleText(article.getTitle());
        return saleArticle;
    }
}
