package com.awesome.app.awesomeapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.awesome.app.awesomeapp.R;
import com.awesome.app.awesomeapp.util.Data;
import com.awesome.app.awesomeapp.util.EventStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private boolean recording = false;
    private boolean hasPermission = false;

    private int samplingRate = 16000;
    private int bufferSize = 400;
    private EventStore mStore;


    String[] permissions = {
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AwesomeFrag", "onActivityCreate0");
        super.onCreate(savedInstanceState);
        Log.d("AwesomeFrag", "onActivityCreate");
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        Log.d("AwesomeFrag", "onActivityCreate2");
        for (String s : permissions)
        {
            if(ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissions,0);
            }
        }
        Log.d("AwesomeFrag", "onActivityCreate3");
       // initialize();
        mStore = EventStore.get(this);
        List<String> registeredEvents = mStore.getRegisteredEvents();

        if(registeredEvents.size() == 0)
        {
            List<String> events = new ArrayList<>();

            for (int k: Data.labelMap.keySet()) {
                events.add(Data.labelMap.get(k));
            }
            mStore.registerEvents(events);
        }
        displaySelectedScreen(R.id.nav_home);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Called when the user answers to the permission dialogs.
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length)) return;
        boolean hasAllPermissions = true;

        for (int grantResult:grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false;
            Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
        }

        if (hasAllPermissions){
            this.hasPermission = true;
        }
    }

    private void initialize() {
        String samplingRateString = null;
        String bufferSizeString = null;

        if(Build.VERSION.SDK_INT >= 17)
        {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null)
            {
                samplingRateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
                bufferSizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            }
        }

        samplingRateString = "16000"; //samplingRateString == null ? "22050" : samplingRateString;
        bufferSizeString = "400"; //bufferSizeString == null ? "480" : bufferSizeString;

        samplingRate = Integer.parseInt(samplingRateString);
        bufferSize = Integer.parseInt(bufferSizeString);
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment() ;
                Bundle bundle = new Bundle();
                bundle.putInt("SamplingRate", samplingRate);
                bundle.putInt("BufferSize", bufferSize);
                bundle.putBoolean("HasPermission", hasPermission);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_events:
                fragment = new EventSelectorFragment();
                break;
        }
        Log.d("AwesomeFrag", "onActivityCreate4");
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        Log.d("AwesomeFrag", "onActivityCreate5");
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }



}
