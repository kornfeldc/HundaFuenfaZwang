package hfz.svoeoggau.at.hundatfuenfazwanzg.classes;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Sale;

/**
 * Created by Christian on 11.03.2018.
 */

public class SaleDay {
    public String day = "";
    public double topay = 0.0;
    public double payed = 0.0;

    public SaleDay(String day, double topay, double payed) {
        this.day = day;
        this.topay = topay;
        this.payed = payed;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getTopay() {
        return topay;
    }

    public void setTopay(double topay) {
        this.topay = topay;
    }

    public double getPayed() {
        return payed;
    }

    public void setPayed(double payed) {
        this.payed = payed;
    }

    public void addSale(Sale sale) {
        if(sale.getDayStr().equals(day)) {
            if(sale.getPayed() == 1)
                payed += sale.getCalculatedSum();
            else
                topay += sale.getSum();
        }
    }

    public static SaleDay getDay(Vector<SaleDay> days, String day) {
        for(SaleDay sd : days) {
            if(sd.getDay().equals(day))
                return sd;
        }
        return null;
    }
}
