package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
