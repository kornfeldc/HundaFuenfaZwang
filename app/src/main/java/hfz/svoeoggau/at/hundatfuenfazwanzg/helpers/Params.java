package hfz.svoeoggau.at.hundatfuenfazwanzg.helpers;

import java.util.HashMap;

/**
 * Created by Christian on 05.03.2018.
 */

public class Params {

    private static int pos = 0;
    private static HashMap<Integer, Object> map = new HashMap<>();

    public static String setParams(Object obj) {
        map.put(pos, obj);
        return String.valueOf(pos);
    }

    public static Object getParams(String id) {
        int pos = Integer.valueOf(id);
        return map.get(pos);
    }
}
