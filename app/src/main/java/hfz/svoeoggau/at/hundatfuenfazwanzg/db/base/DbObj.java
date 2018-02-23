package hfz.svoeoggau.at.hundatfuenfazwanzg.db.base;

import java.util.List;

/**
 * Created by Christian on 23.02.2018.
 */

public class DbObj {

    public interface OnLoadSingle {
        void callback(Object obj);
    }

    public interface OnLoadList {
        void callback(List<Object> obj);
    }
}
