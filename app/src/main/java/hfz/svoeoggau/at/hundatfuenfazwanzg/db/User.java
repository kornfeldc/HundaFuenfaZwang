package hfz.svoeoggau.at.hundatfuenfazwanzg.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import hfz.svoeoggau.at.hundatfuenfazwanzg.db.base.DbObj;

/**
 * Created by Christian on 23.02.2018.
 */

public class User extends DbObj {

    public static final String COLLECTION = "users";
    public static final String FIELD_USERNAME = "userName";
    public static final String FIELD_LOGIN = "login";
    public static final String FIELD_ISADMIN = "isAdmin";

    private String userName="";
    private String login="";
    private boolean isAdmin = false;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void parseMap(Map<String,Object> map) {
        setLogin(map.get(FIELD_LOGIN).toString());
        setUserName(map.get(FIELD_USERNAME).toString());
        setAdmin((boolean)map.get(FIELD_ISADMIN));
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_LOGIN, getLogin());
        map.put(FIELD_USERNAME, getUserName());
        map.put(FIELD_ISADMIN, isAdmin());
        return map;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION).document(login)
                .set(getMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public static void getByLogin(String login, final OnLoadSingle ols) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .document(login)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            User user = new User();
                            user.parseMap(documentSnapshot.getData());
                            ols.callback(user);
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
}
