package android.pillcentral;

/**
 * Created by Admin on 7/9/2017.
 */

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class Tabs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);


        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_daily_dark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_weekly_dark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_home_dark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_allpills_dark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_settings_dark));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(2, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addpill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(Tabs.this, addpill.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    pilldisplay tab1 = new pilldisplay();
                    Bundle b1 = new Bundle();
                    b1.putString("username", getIntent().getStringExtra("username"));
                    tab1.setArguments(b1);
                    return tab1;
                case 1:
                    weeklydisplay tab2 = new weeklydisplay();
                    Bundle b2 = new Bundle();
                    b2.putString("username", getIntent().getStringExtra("username"));
                    tab2.setArguments(b2);
                    return tab2;
                case 2:
                    homescreen tab3 = new homescreen();
                    Bundle b3 = new Bundle();
                    b3.putString("username", getIntent().getStringExtra("username"));
                    tab3.setArguments(b3);
                    return tab3;
                case 3:
                    distinctpills tab4 = new distinctpills();
                    Bundle b4 = new Bundle();
                    b4.putString("username", getIntent().getStringExtra("username"));
                    tab4.setArguments(b4);
                    return tab4;
                case 4:
                    settings tab5 = new settings();
                    Bundle b5 = new Bundle();
                    b5.putString("username", getIntent().getStringExtra("username"));
                    tab5.setArguments(b5);
                    return tab5;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
