package hfz.svoeoggau.at.hundatfuenfazwanzg;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.ArticlesFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.PersonsFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.SalesFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.SettingsFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.fragments.StatisticsFragment;

public class MainActivity extends BaseActivity {

    private TextView mTextMessage;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sale:
                    changeFragment(new SalesFragment());
                    return true;
                case R.id.navigation_persons:
                    changeFragment(new PersonsFragment());
                    return true;
                case R.id.navigation_articles:
                    changeFragment(new ArticlesFragment());
                    return true;
                case R.id.navigation_statistics:
                    changeFragment(new StatisticsFragment());
                    return true;
                case R.id.navigation_settings:
                    changeFragment(new SettingsFragment());
                    return true;
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

        changeFragment(new SalesFragment());
    }

}
