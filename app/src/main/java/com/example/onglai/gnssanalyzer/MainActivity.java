package com.example.onglai.gnssanalyzer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    private static final int LOCATION_REQUEST_ID = 1;

    public static MainActivity mActivity;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private GnssContainer mGnssContainer;
    private SatelliteFragment mSatelliteFragment;
    private MapFragment mMapFragment;
    private ReportFragment mReportFragment;
    private SettingsFragment mSettingsFragment;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SatelliteFragment()).commit();
            mNavigationView.setCheckedItem(R.id.nav_satellite);
        }

        requestPermissionsAndSetupFragment(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGnssContainer.unregisterAll();
    }

    static MainActivity getInstance() { return mActivity; }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.nav_satellite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SatelliteFragment()).commit();
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;
            case R.id.nav_report:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ReportFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupFragments() {
        mSatelliteFragment = new SatelliteFragment();
        mMapFragment = new MapFragment();
        mReportFragment = new ReportFragment();
        mSettingsFragment = new SettingsFragment();

        //To be implemented
        mGnssContainer =
                new GnssContainer(
                        getApplicationContext(),
                        mSatelliteFragment,
                        mMapFragment);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mGnssContainer.registerGnssMeasurements();
            mGnssContainer.registerGnssStatusChanged();
            mGnssContainer.registerLocation();
        }

    }

    private boolean hasPermission(Activity activity) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permission granted at install time
            return true;
        } else {
            for(String p : REQUIRED_PERMISSIONS) {
                if(ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermissionsAndSetupFragment(Activity activity) {
        if(hasPermission(activity)) {
            setupFragments();
        } else {
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, LOCATION_REQUEST_ID);
        }
    }
}
