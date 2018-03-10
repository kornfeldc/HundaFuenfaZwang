package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import java.util.Locale;

/**
 * Created by Christian on 06.03.2018.
 */

public class Format {
    public static String doubleToCurrency(double val) {
        return "€ "+String.format("%.2f", val);
    }

    public static String doubleToString(double val) {
        return String.format("%.2f", val);
    }

    public static double stringToDouble(String val) {
        if(!val.isEmpty())
            return Double.valueOf(val.replace(",","."));
        return 0;
    }

    public static String capitalize(final String line) {
        if(line.length() > 0)
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        return "";
    }
}
