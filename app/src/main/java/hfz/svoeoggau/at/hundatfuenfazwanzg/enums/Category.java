package hfz.svoeoggau.at.hundatfuenfazwanzg.enums;

/**
 * Created by Christian on 23.02.2018.
 */

public class Category {

    public static final String DRINK_ALCOHOLIC = "alcoholic";
    public static final String DRINK_NONALCOHOLIC = "nonalcoholic";
    public static final String FOOD_MEAL = "meal";
    public static final String FOOD_SWEETS = "sweets";

    public static final String[] ARR = new String[] { DRINK_ALCOHOLIC, DRINK_NONALCOHOLIC, FOOD_MEAL, FOOD_SWEETS};

    public static final Integer getIndex(String category) {
        switch(category) {
            case DRINK_ALCOHOLIC:
                return 0;
            case DRINK_NONALCOHOLIC:
                return 1;
            case FOOD_MEAL:
                return 2;
            case FOOD_SWEETS:
                return 3;
        }
        return -1;
    }
}
