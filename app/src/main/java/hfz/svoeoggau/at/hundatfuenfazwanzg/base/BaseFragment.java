package hfz.svoeoggau.at.hundatfuenfazwanzg.base;

import android.app.Fragment;

import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.UI;

/**
 * Created by Christian on 23.02.2018.
 */

public class BaseFragment extends Fragment {
    public interface OnSearch {
        void search(String search);
    }

    private OnSearch onSearch = null;

    public void setOnSearchListener(OnSearch onSearch) {
        this.onSearch = onSearch;
    }

    public void executeSearch(String search) {
        if(onSearch != null)
            onSearch.search(search);
    }

    public void showProgress() {
        UI.showProgress(getView());
    }

    public void hideProgress() {
        UI.hideProgress(getView());
    }
}
