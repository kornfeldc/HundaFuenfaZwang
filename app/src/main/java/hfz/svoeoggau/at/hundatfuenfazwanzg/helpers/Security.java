package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;

/**
 * Created by Christian on 10.03.2018.
 */

public class Security {

    private final static String UPREF = "upref";

    public final static String[] USERNAMES = new String[] {"emu", "ck", "kk", "bar", "hk", "tm", "tu", "vs", "ms"};

    /*
    SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
SharedPreferences.Editor prefsEditor;
prefsEditor = myPrefs.edit();
//strVersionName->Any value to be stored
prefsEditor.putString("STOREDVALUE", strVersionName);
prefsEditor.commit();

//Get Preferenece
SharedPreferences myPrefs;
myPrefs = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
String StoredValue=myPrefs.getString("STOREDVALUE", "");
     */

    public static boolean auth(Context context, String user, String password) {
        for(String u : USERNAMES) {
            if(user.equals(u) && password.equals("og125")) {
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
