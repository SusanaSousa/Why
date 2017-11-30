package edu.feup.dansus.why_maps;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class WhyActivity extends AppCompatActivity {

    private FragmentManager fragMan;
    private DrawerLayout drawer;
    private NavigationView navView;
    private static final int LOCATION_REQUEST = 1;
    private String[] locPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_maps_main);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Getting the fragment manager
        fragMan = getSupportFragmentManager();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        setupDrawer(navView);


        // Context monitor is the default fragment
        checkAndLaunchContextMonitor();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.why_maps_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer(NavigationView navView){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                launchDrawerFragments(item);
                item.setChecked(false);
                return true;
            }
        });
    }

    private void launchDrawerFragments(MenuItem item){
        // Launch fragment based on the clicked item
        Fragment frag = null;
        Class fragmentClass = null;

        int value = item.getItemId();

        switch (value) {
            case R.id.context_nav:

                // Setting the item as checked
                item.setChecked(true);

                // Setting the Action Bar title
                setTitle(item.getTitle());

                drawer.closeDrawers();

                checkAndLaunchContextMonitor();

                return; // We finish here, in this case

            case R.id.events_nav:
                fragmentClass = EventsFrag.class;
                break;

            case R.id.devices_nav:
                fragmentClass = DevicesFrag.class;
                break;

            case R.id.report_nav:
                fragmentClass = SumReportFrag.class;
                break;

            case R.id.settings_nav:
                fragmentClass = SettingsFrag.class;
                break;

            case R.id.help_nav:
                // Launching the intent
                Intent cmebWebIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://sigarra.up.pt/feup/pt/ucurr_geral.ficha_uc_view?pv_ocorrencia_id=406933"));

                try {
                    startActivity(cmebWebIntent);
                } catch (ActivityNotFoundException e) { // Prepare for the worst, a web browser may not be installed!
                    Toast.makeText(this, "Please install a web browser to access help", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.about_nav:
                fragmentClass = AboutFrag.class ;
                break;

            default:
                fragmentClass = EventsFrag.class;
        }

        launchFrag(fragmentClass);

        // Setting the item as checked (but not on Help and About menus - visually it looks bad)
        if (item.getItemId() != R.id.help_nav || item.getItemId() != R.id.about_nav) {
            item.setChecked(true);
        }

        // Setting the Action Bar title
        setTitle(item.getTitle());

        // Closing the drawer
        drawer.closeDrawers();
    }

    private void checkAndLaunchContextMonitor(){
        // Checking for the required location permissions
        if ((ActivityCompat.checkSelfPermission(this, locPermissions[0]) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, locPermissions[1]) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[] {locPermissions[0], locPermissions[1]}, LOCATION_REQUEST); // Requesting the permissions
        } else { // If permissions exists, we launch the fragment directly
            launchFrag(ContextMonitorFrag.class);
        }
    }




    // Handling the permission request reply by the user
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                launchFrag(ContextMonitorFrag.class);
            }
            else{
                checkAndLaunchContextMonitor(); // Recursive until we get the permission
            }
        }
    }

    private void launchFrag(Class fragClass){
            Fragment frag = null;

            try {
                frag = (Fragment) fragClass.newInstance();
                fragMan.beginTransaction().replace(R.id.frag_accepter, frag).commit();
            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }
