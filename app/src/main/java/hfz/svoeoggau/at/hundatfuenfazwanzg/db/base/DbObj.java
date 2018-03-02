package hfz.svoeoggau.at.hundatfuenfazwanzg.db.base;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Created by Christian on 23.02.2018.
 */

public class DbObj {

    @Exclude
    private DocumentReference reference;

    @Exclude
    public DocumentReference getReference() {
        return reference;
    }

    @Exclude
    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public interface OnLoadSingle {
        void callback(Object obj);
    }

    public interface OnLoadList {
        void callback(List<Object> obj);
    }

    public interface OnListChanged {
        void callback();
    }

    @Exclude
    public boolean isIdSet() {
        return (reference != null && reference.getId() != null);
    }

    @Exclude
    public String getId() {
        if(isIdSet())
            return reference.getId();
        return "";
    }
}
