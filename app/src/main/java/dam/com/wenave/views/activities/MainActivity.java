package dam.com.wenave.views.activities;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

import java.util.List;
import java.util.Locale;

import dam.com.wenave.BuildConfig;
import dam.com.wenave.R;
import dam.com.wenave.managements.CameraManagement;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.views.fragments.PreferenceFragment;
import dam.com.wenave.views.fragments.RouteInfoFragment;
import dam.com.wenave.views.fragments.MainMapFragment;
import dam.com.wenave.managements.LocationManagement;
import dam.com.wenave.managements.MapBoxManagement;
import dam.com.wenave.models.Route;
import dam.com.wenave.services.interfaces.IFragmentLaucher;
import dam.com.wenave.views.fragments.RoutesFragment;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity implements
        PermissionsListener,
        NavigationView.OnNavigationItemSelectedListener,
        IFragmentLaucher
{

    private static final int REQUEST_SEARCH_PLACE_CODE = 123 ;
    public MapBoxManagement management = new MapBoxManagement();

    public MainMapFragment mainFragment;
    public Fragment currentFragment;

    public Location usersLocation;

    private boolean isInPreferences = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Display the version
        ((TextView)findViewById(R.id.versionTag)).setText(String.format(Locale.getDefault(), "WeNave v%s", BuildConfig.VERSION_NAME));

        setUpRealm();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainFragment = new MainMapFragment(); //MainMapFragment.buildFragment(management);
        currentFragment = mainFragment;

        //Fragment miFragment = new RouteInfoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mainFragment).commit();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setUpRealm()
    {
        Realm.init(this);
        RealmConfiguration decodeConfig = new RealmConfiguration.Builder()
                .name("decodeapp.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(decodeConfig);

        DataAccessLayer.setUpDb(decodeConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SEARCH_PLACE_CODE)
        {
            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            Point p = (Point) selectedCarmenFeature.geometry();

            if (p != null)
            {
                // Move map camera to the selected location
                CameraManagement.animateCamera(management.mapbox, p, 1000);
            }
            else
            {
                Log.e("WeNAVE", "An error occurred when processing the requestes point");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        // We will Stop the app only when we press back being in the MainMapFragment
        else if (currentFragment instanceof MainMapFragment && ! isInPreferences)
        {
            super.onBackPressed();
        }
        // Else we will go there
        else
        {
            changeFragment(MainMapFragment.class, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.create_route)
        {
            changeFragment(MainMapFragment.class, null, null);
        }
        else if (id == R.id.route)
        {
            changeFragment(RoutesFragment.class, null, null);
        }
        else if (id == R.id.settings)
        {
            changeFragment(PreferenceFragment.class, null, null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(Class<? extends Fragment> fClass, Route route, Boolean existing)
    {
        try
        {
            Fragment f = fClass.newInstance();

            if (f instanceof MainMapFragment)
            {
                f = new MainMapFragment();//MainMapFragment.buildFragment(management);
            }
            else if (f instanceof RouteInfoFragment)
            {
                f = RouteInfoFragment.buildFragment(route, existing);
            }

            // We come from the RouteInfoFragment and we don't want to add the Route
            if (currentFragment instanceof RouteInfoFragment && !((RouteInfoFragment) currentFragment).isAdding())
            {
                // Check the fragment we are going on to is not the Preferences
                if (! (f instanceof PreferenceFragment) && ! isInPreferences)
                {
                    DataAccessLayer.deleteRoute(((RouteInfoFragment) currentFragment).route);
                }
            }

            // We don't need to save PreferenceScreen
            // since user cannot interact with other fragments
            // but preference's one.
            // We keep the previous fragment to allow switch after a style change.
            if (f instanceof PreferenceFragment)
            {
                isInPreferences = true;
            }
            else
            {
                currentFragment = f;
                isInPreferences = false;
            }

            // Inflate the selected fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, f)
                    .commit();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(this,"User location permission needed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted)
        {
            management.mapbox.getStyle(style ->
            {
                LocationManagement.enableLocationComponent(MainActivity.this, management.mapbox, style);
            });
        }
        else
        {
            Toast.makeText(this, "User location permission not granted", Toast.LENGTH_LONG).show();

            // We disable location and circuit modes since we need the location for both
            mainFragment.locationMode.setEnabled(false);
            mainFragment.circuitMode.setEnabled(false);

            mainFragment.locationModeHint.setTextColor(getResources().getColor(R.color.mapboxGrayLight));
            mainFragment.circuitModeHint.setTextColor(getResources().getColor(R.color.mapboxGrayLight));
            //finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        LocationManagement
                .permissionsManager
                .onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void launchRouteFragment(String routeId)
    {
        management.clearPoints();
        setUpRealm();

        Route r = (Route) DataAccessLayer.getRouteById(routeId);
        changeFragment(RouteInfoFragment.class, r, false);
    }

    @Override
    public void launchExistingRoute(Route route)
    {
        management.clearPoints();
        changeFragment(RouteInfoFragment.class, route, true);
    }
}
