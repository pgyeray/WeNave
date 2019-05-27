package dam.com.wenave.views.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dam.com.wenave.enums.Enums;
import dam.com.wenave.managements.CameraManagement;
import dam.com.wenave.managements.StyleManagement;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.R;
import dam.com.wenave.managements.MapBoxManagement;

import static dam.com.wenave.enums.Enums.Mode.CIRCUIT_MODE;
import static dam.com.wenave.enums.Enums.Mode.LOCATION_MODE;
import static dam.com.wenave.enums.Enums.Mode.NORMAL_MODE;

public class MainMapFragment extends Fragment
{
    static
    {
        System.loadLibrary("keys");
    }

    public static native String getMapboxUrl();

    private final static String TOKEN = getMapboxUrl();
    private static final int REQUEST_SEARCH_PLACE_CODE = 501;

    private MapBoxManagement management;
    private Date date;
    private MapView mapView;
    private FloatingActionButton addRoute;
    private FloatingActionButton clear;
    private FloatingActionButton search;
    private FloatingActionButton calendar;

    private FloatingActionButton normalMode;
    public FloatingActionButton circuitMode;
    public FloatingActionButton locationMode;

    private TextView normalModeHint;
    public TextView locationModeHint;
    public TextView circuitModeHint;

    private TextView optionLabel;

    public Enums.Mode currentMode = NORMAL_MODE;

    private Boolean isFABOpen = false;

    private MainActivity context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        context = (MainActivity) getActivity();
        Mapbox.getInstance(context, TOKEN);

        management = context.management;

        View rootview = inflater.inflate(R.layout.fragment_map_create_route, container, false);

        mapView = (MapView) rootview.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap ->
        {
            management.setUpMapBox(context, mapboxMap, mapView);
        });

        addRoute = rootview.findViewById(R.id.route);
        clear = rootview.findViewById(R.id.delete);
        search = rootview.findViewById(R.id.search_location);
        calendar = rootview.findViewById(R.id.datepicker);

        normalMode = rootview.findViewById(R.id.normal_mode);
        circuitMode = rootview.findViewById(R.id.circuit_mode);
        locationMode = rootview.findViewById(R.id.location_mode);

        normalModeHint = rootview.findViewById(R.id.normal_mode_hint);
        circuitModeHint = rootview.findViewById(R.id.circuit_mode_hint);
        locationModeHint = rootview.findViewById(R.id.location_mode_hint);

        optionLabel = (TextView) rootview.findViewById(R.id.option_info);
        setOptionLabel();

        normalMode.setOnClickListener((v) ->
        {
            //v.setBackgroundColor(context.getResources().getColor(R.color.selected_cardview));
            normalModeHint.setTextColor(context.getResources().getColor(R.color.mapbox_blue));

            if (currentMode == LOCATION_MODE)
            {
                ((TextView)rootview.findViewById(R.id.location_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }
            if (currentMode == CIRCUIT_MODE)
            {
                ((TextView)rootview.findViewById(R.id.circuit_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }

            currentMode = NORMAL_MODE;
            checkAddRouteVisibility(2, 1);
        });

        circuitMode.setOnClickListener((v) ->
        {
            //v.setcolor(context.getResources().getColor(R.color.selected_cardview));
            circuitModeHint.setTextColor(context.getResources().getColor(R.color.mapbox_blue));

            if (currentMode == LOCATION_MODE)
            {
                ((TextView)rootview.findViewById(R.id.location_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }
            if (currentMode == NORMAL_MODE)
            {
                ((TextView)rootview.findViewById(R.id.normal_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }

            currentMode = CIRCUIT_MODE;
            checkAddRouteVisibility(2, 1);
        });

        locationMode.setOnClickListener((v) ->
        {
            //v.setBackgroundColor(context.getResources().getColor(R.color.selected_cardview));
            locationModeHint.setTextColor(context.getResources().getColor(R.color.mapbox_blue));

            if (currentMode == CIRCUIT_MODE)
            {
                ((TextView)rootview.findViewById(R.id.circuit_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }
            if (currentMode == NORMAL_MODE)
            {
                ((TextView)rootview.findViewById(R.id.normal_mode_hint))
                        .setTextColor(StyleManagement.getCurrentLabelColor());
            }

            currentMode = LOCATION_MODE;
            checkAddRouteVisibility(2, 1);
        });

        //Click-listeners
        FloatingActionButton more = (FloatingActionButton) rootview.findViewById(R.id.more);
        more.setOnClickListener(v ->
        {
            if(!isFABOpen)
            {
                showFABMenu();
            }
            else
            {
                closeFABMenu();
            }
        });

        search.setOnClickListener(view ->
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                                    .backgroundColor(Color.parseColor("#EEEEEE"))
                                    .limit(10)
                                    .build(PlaceOptions.MODE_CARDS))
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_SEARCH_PLACE_CODE);
        });

        calendar.setOnClickListener(v ->
        {
            final Calendar calendar = Calendar.getInstance();
            int i_day = calendar.get(Calendar.DAY_OF_MONTH);
            int i_month = calendar.get(Calendar.MONTH);
            int i_year = calendar.get(Calendar.YEAR);
            int i_hour = calendar.get(Calendar.HOUR_OF_DAY);
            int i_minute = calendar.get(Calendar.MINUTE);

            new DatePickerDialog(context, (view, year, month, dayOfMonth) ->
            {
                new TimePickerDialog(getActivity(),
                        (vieww, hourOfDay, minute) ->
                        {
                            date = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute).getTime();
                            management.setTime(date);

                            Toast.makeText(context, "Date changed", Toast.LENGTH_LONG).show();
                        }, i_hour, i_minute, true)
                        .show();
            }, i_year, i_month, i_day).show();
        });

        addRoute.setOnClickListener((v) ->
        {
            int minPoints = (currentMode == NORMAL_MODE) ? 2 : 1;
            if (management.points.size() >= minPoints)
            {
                closeFABMenu();

                management.fitPointsAsMode(currentMode);
                management.addRoute(context);
            }
            else
            {
                Toast.makeText(context, "You need at least two points", Toast.LENGTH_LONG).show();
            }
        });

        clear.setOnClickListener(v ->
        {
            management.clearPoints();
        });
        return rootview;
    }

    private void showFABMenu()
    {
        isFABOpen=true;
        calendar.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        search.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        clear.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        //clear.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
    }

    private void closeFABMenu()
    {
        isFABOpen=false;
        //addRoute.animate().translationY(0);
        calendar.animate().translationY(0);
        search.animate().translationY(0);
        clear.animate().translationY(0);
    }

    /**
     * We set the AddRoute button visibility depending on how many points we do have
     *      Normal Mode: We need et least 2 points,
     *      Location Mode and Circuit Mode: We need at least 1 point, since out location counts
     *
     * Warning: we are counting points, not marks because of potencial bugs
     * @param minPointsOnNormal
     * @param minPointsOnOther
     */
    public void checkAddRouteVisibility(int minPointsOnNormal, int minPointsOnOther)
    {
        int minPoints = (currentMode == NORMAL_MODE) ? minPointsOnNormal : minPointsOnOther;
        if (management.points.size() < minPoints)
        {
            setAddRouteVisibility(View.INVISIBLE);
        }
        else
        {
            setAddRouteVisibility(View.VISIBLE);
        }
    }

    public void setAddRouteVisibility(final int VISIBILITY)
    {
        addRoute.setVisibility(VISIBILITY);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();

        Toast.makeText(context,
                String.format(Locale.getDefault(),"You should consider saving battery\nWeNave consumes quite battery"),
                Toast.LENGTH_SHORT)
                .show();
    }

    @SuppressLint("LogNotTimber")
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SEARCH_PLACE_CODE)
        {
            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Move map camera to the selected location
            Point p = (Point) selectedCarmenFeature.geometry();

            if (p != null)
            {
                // Move map camera to the selected location
                CameraManagement.animateCamera(management.mapbox, p, 1000);
            }
            else
            {
                Log.e("WeNAVE", "Error on selected location");
            }
        }
    }

    void setOptionLabel()
    {
        String option = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("route_options", "Driving"); // check if-null
        optionLabel.setText(option);
    }
}
