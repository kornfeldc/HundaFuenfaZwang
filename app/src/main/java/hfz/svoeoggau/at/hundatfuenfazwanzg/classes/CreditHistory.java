package hfz.svoeoggau.at.hundatfuenfazwanzg.classes;

import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.DF;

/**
 * Created by Christian on 03.03.2018.
 */

public class CreditHistory {
    private String day;
    private Double credit;

    public CreditHistory() {}
    public CreditHistory(double credit) {
        this.credit = credit;
        this.day = DF.CalendarToString(DF.Now());
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }
}
