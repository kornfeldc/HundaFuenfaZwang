package hfz.svoeoggau.at.hundatfuenfazwanzg;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import hfz.svoeoggau.at.hundatfuenfazwanzg.base.AuthedActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.ArticlesFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.PersonsFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.SalesFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.SettingsFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.StatisticsFragment;

public class MainActivity extends AuthedActivity {

    private TextView mTextMessage;
    private BaseFragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.navigation_sale:
                    fragment = new SalesFragment();
                    changeFragment(fragment);
                    getSupportActionBar().setTitle(R.string.title_sale);
                    return true;
                case R.id.navigation_persons:
                    fragment = new PersonsFragment();
                    changeFragment(fragment);
                    getSupportActionBar().setTitle(R.string.title_persons);
                    return true;
                case R.id.navigation_articles:
                    fragment = new ArticlesFragment();
                    changeFragment(fragment);
                    getSupportActionBar().setTitle(R.string.title_articles);
                    return true;
                /*case R.id.navigation_statistics:
                    fragment = new StatisticsFragment();
                    changeFragment(fragment);
                    getSupportActionBar().setTitle(R.string.title_statistics);
                    return true;
                case R.id.navigation_settings:
                    fragment = new SettingsFragment();
                    changeFragment(fragment);
                    getSupportActionBar().setTitle(R.string.title_settings);
                    return true;*/
            }
            return false;
        }
    };

    private void changeFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragment = new SalesFragment();
        changeFragment(fragment);
        getSupportActionBar().setTitle(R.string.title_sale);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(fragment != null) fragment.executeSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(fragment != null) fragment.executeSearch(newText);
                return false;
            }
        });
        return true;

    }
}
