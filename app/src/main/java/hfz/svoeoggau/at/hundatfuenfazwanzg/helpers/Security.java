package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;

/**
 * Created by Christian on 10.03.2018.
 */

public class Security {

    private final static String PWHASH = "cb41d253e2c5911dfcbd61c437104875";
    private final static String UPREF = "upref";

    public static String[] getUserNames(Context context) {
        String usersCsv = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("users.csv"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                usersCsv+=mLine;
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exceptionck
                }
            }
        }

        return usersCsv.split(",");
    }

    public static boolean auth(Context context, String user, String password) {
        for(String u : getUserNames(context)) {
            if(user.equals(u) && Format.md5(password).equals(PWHASH)) {
                SharedPreferences myPrefs = context.getSharedPreferences(UPREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor;
                prefsEditor = myPrefs.edit();
                prefsEditor.putString("user", user);
                prefsEditor.commit();
                return true;
            }
        }
        return false;
    }

    public static Boolean isAuthed(Context context, boolean showError) {
        if(context != null) {
            String user = getUser(context);
            if(!user.isEmpty())
                return true;
            else {
                if(showError)
                    Toast.makeText(context, R.string.auth_failed, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else
            return false;
    }

    public static String getUser(Context context) {
        if(context != null) {
            SharedPreferences myPrefs = context.getSharedPreferences(UPREF, Context.MODE_PRIVATE);
            return myPrefs.getString("user", "");
        }
        else
            return "";
    }
}
