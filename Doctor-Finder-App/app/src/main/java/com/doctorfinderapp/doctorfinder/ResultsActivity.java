package com.doctorfinderapp.doctorfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.doctorfinderapp.doctorfinder.access.SplashActivity;
import com.doctorfinderapp.doctorfinder.fragment.DoctorListFragment;
import com.doctorfinderapp.doctorfinder.fragment.DoctorMapsFragment;

import com.doctorfinderapp.doctorfinder.functions.AddDoctors;
//import com.doctorfinderapp.doctorfinder.fragment.SearchFragment;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import com.doctorfinderapp.doctorfinder.UserProfileActivity;


public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //adding doctors data
        AddDoctors.addData();

        ParseUser currentUser = ParseUser.getCurrentUser();
//         if(currentUser.getEmail()!=null)Toast.makeText(getApplicationContext(), "Logged in as "+currentUser.getEmail(), Toast.LENGTH_LONG).show();
  //      else Toast.makeText(getApplicationContext(), "Logged in with Facebook", Toast.LENGTH_LONG).show();
        //set status bar color because in xml don't work
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }*/

        setContentView(R.layout.activity_results);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //find fab buttons
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);

        //load animation
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        //onClick button
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Give the TabLayout
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        //menu icon
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Set behavior of Navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }



    // Add Fragments to Tabs------------------------------------------------------------------------
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new DoctorListFragment(), "Lista");
        adapter.addFragment(new DoctorMapsFragment(), "Mappa");

        viewPager.setAdapter(adapter);
    }


    //search view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //no inspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
            /**TODO*/
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.exit:
                super.finish();
            case R.id.about:

            case R.id.profile:
                Intent intent_user = new Intent(ResultsActivity.this, UserProfileActivity.class);
                startActivity(intent_user);

            //caricare sito
            case R.id.logout:
                ParseUser.logOut();
                Log.d("R", "Logged out");
                Toast.makeText(getApplicationContext(),
                        "Logged out",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ResultsActivity.this, SplashActivity.class);
                startActivity(intent);


        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.fab:
                animateFAB();
                break;

            case R.id.fab1:
                //TODO
                break;

            case R.id.fab2:
                //TODO
                break;
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {



            return mFragmentTitleList.get(position);
        }
    }//---------------------------------------------------------------------------------------------

    //animation fab buttons
    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("button", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("button","open");

        }
    }
}


