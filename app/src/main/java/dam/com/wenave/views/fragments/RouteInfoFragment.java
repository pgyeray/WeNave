package dam.com.wenave.views.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.services.MapBoxService;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.R;
import dam.com.wenave.managements.MapBoxManagement;
import dam.com.wenave.models.Route;
import dam.com.wenave.views.adapters.RVAdapter;
import dam.com.wenave.views.adapters.UpdateSelectionAdapter;

public class RouteInfoFragment extends Fragment
{
    static
    {
        System.loadLibrary("keys");
    }

    @SuppressLint("KeepMissing")
    public static native String getMapboxUrl();

    private final static String TOKEN = getMapboxUrl();

    public static final String ROUTE_KEY = "route_key";

    private MapView mapView;
    private MapBoxManagement management = new MapBoxManagement();
    public Route route;
    MainActivity context;

    private boolean existing;
    private boolean adding;

    public RecyclerView rv;
    private FloatingActionButton saveData;
    private FloatingActionButton refreshData;

    public static RouteInfoFragment buildFragment(Route route, boolean existing)
    {
        RouteInfoFragment fragment = new RouteInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ROUTE_KEY, route);
        fragment.setArguments(bundle);

        fragment.existing = existing;
        fragment.adding = existing;

        return fragment;
    }

    /**
     * Setting data before
     * @param savedInstanceState bundle where data is contained
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            route = (Route) getArguments().getSerializable(ROUTE_KEY);
        }

        context = (MainActivity) getActivity();
        if (context != null)
        {
            Mapbox.getInstance(context, TOKEN);
        }
        else
        {
            Log.e("WeNAVE", "Fragment type " + this.getClass() + " has no valid context");
        }
    }

    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_info_route, container, false);

        saveData = (FloatingActionButton) rootView.findViewById(R.id.saveData);
        refreshData = (FloatingActionButton) rootView.findViewById(R.id.refreshData);

        ((TextView)rootView
                .findViewById(R.id.option_info_exist))
                .setText(route.getOption());

        rv = (RecyclerView) rootView.findViewById(R.id.route_forecasts);
        rv.setLayoutManager(new LinearLayoutManager(context));

        if (existing)
        {
            refreshData.setVisibility(View.VISIBLE);
            saveData.setVisibility(View.INVISIBLE);
        }
        else
        {
            saveData.setVisibility(View.VISIBLE);
            refreshData.setVisibility(View.INVISIBLE);
        }

        mapView = (MapView) rootView.findViewById(R.id.mapViewRoute);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapBoxMap ->
        {
            management.setUpMapBoxRoute(context, mapBoxMap, mapView, this);
        });

        saveData.setOnClickListener(v ->
        {
            showAddItemDialog(context);
        });

        refreshData.setOnClickListener((v) ->
        {
            new DialogUpdateSelection(this)
                    .show(getFragmentManager(), "dialogSelector");
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean("key_refresh_data", true) && existing)
        {
            updateRoute(null);
        }

        return rootView;
    }



    public void setRoute(Route route)
    {
        this.route = route;
    }

    private void showAddItemDialog (final Context c)
    {
        final EditText taskEditText = new EditText(c);

        int marginX = dpToPx(50);
        int marginY = dpToPx(20);

        @SuppressLint("RestrictedApi") AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(c.getString(R.string.dialog_add_route))
                .setView(taskEditText, marginX, marginY, marginX, marginY)
                .setPositiveButton(c.getString(R.string.dialog_button_add), (dialog1, which) ->
                {
                    addRoute(String.valueOf(taskEditText.getText()));
                })
                .setNegativeButton(c.getString(R.string.dialog_button_cancel), null)
                .create();
        dialog.show();
    }

    /**
     * Create the route instance into the database
     * @param routeName The name setted by the user
     */
    private void addRoute(String routeName)
    {
        DataAccessLayer.beginTransaction();

        route.routeTitle = routeName;
        route.setCreationDate(new Date());
        route.setModifiedDate(new Date());
        DataAccessLayer.addRoute(route);

        DataAccessLayer.commitTransaction();

        adding = true;
        context.changeFragment(RoutesFragment.class, null, null);
    }

    /**
     * Updates the Forecast of a route, and shows it back
     * @param startDate the date to start the route null if undefined
     */
    void updateRoute (final Date startDate)
    {
        // Avoid SPAM refresh
        final long requestResponse = (Route.requestModification(route, startDate));
        if (requestResponse < 0)
        {
            MapBoxService.getUpdatedRoute(context, route.getId(), startDate, (routeId) ->
            {
                DataAccessLayer.setUpDb(null); // reseting the realm instance

                route = DataAccessLayer.getRouteById(routeId);

                rv.setAdapter(new RVAdapter(route.getRoutePoints(), context, management));
                Toast.makeText(context, "Route updated", Toast.LENGTH_SHORT).show();

                // Route is already drawn, so we don't need to redraw
                // We only change the forecast cardviews
                //management.showRoute(context, mapView, route);
            });
        }
        else
        {
            Toast.makeText(context, String.format(Locale.getDefault(),
                    "Data is already updated\nPlease wait for %d minutes.", requestResponse),
                    Toast.LENGTH_LONG).show();
        }
    }

    private static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    void showDatePickerAndUpdate()
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
                        Date date = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute).getTime();
                        updateRoute(date);
                    }, i_hour, i_minute, true)
                    .show();
        }, i_year, i_month, i_day).show();
    }

    public boolean isExisting()
    {
        return existing;
    }

    public boolean isAdding()
    {
        return adding;
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
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
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
}
