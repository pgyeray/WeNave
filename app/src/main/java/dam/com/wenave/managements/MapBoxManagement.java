package dam.com.wenave.managements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import dam.com.wenave.enums.Enums;
import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.R;
import dam.com.wenave.models.Route;
import dam.com.wenave.services.MapBoxService;
import dam.com.wenave.views.adapters.RVAdapter;
import dam.com.wenave.views.fragments.MainMapFragment;
import dam.com.wenave.views.fragments.RouteInfoFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dam.com.wenave.enums.Enums.Mode.CIRCUIT_MODE;
import static dam.com.wenave.enums.Enums.Mode.LOCATION_MODE;

/**
 * MapBoxManagement will be instanciated by MainMapFragment and RouteInfoFragment</br>
 * We must configurate each as different way.</br>
 *
 * MainMapFragment: sets up with MapBoxManagement#setUpMapBox</br>
 *      List<Point> points (where the route must be generated) is known here</br>
 *          (MapBoxManagement#clearPoints, MapBoxManagement#fitPointsAsMode, MapBoxManagement#addRoute)</br>
 *      Date time (when the route must be generated) is known here</br>
 *          (MapBoxManagement#setTime, MapBoxManagement#addRoute)</br>
 *      MainActivity activity: context where the management is called</br>
 *      Fragment currentFragment: parent of the instance
 *
 * RouteInfoFragment: sets up with MapBoxManagement#setupMapBoxRoute
 *      Needs the context, a route and a recycler view given by its parent fragment
 *      (MapBoxManagement#showRoute) prints the route in the current map view given by its parent
 *
 */
public class MapBoxManagement
{
    static
    {
        System.loadLibrary("keys");
    }

    @SuppressLint("KeepMissing")
    public static native String getMapboxUrl();

    private final static String TOKEN = getMapboxUrl();
    private final static int MAX_POINTS = 25;

    public MapboxMap mapbox;
    private NavigationMapRoute navigationMapRoute;

    public ArrayList<CustomPoint> points; // MainMapFragment
    private List<UUID> ids; // MainMapFragment

    private MainActivity activity; // MainMapFragment
    private Fragment currentFragment;

    private Date time;

    // Initialize the Manager for MainMapFragment
    @SuppressLint("LogNotTimber")
    public void setUpMapBox(final MainActivity context, MapboxMap _mapbox, final MapView mapView)
    {
        activity = context;
        currentFragment = context.currentFragment;

        ids = new ArrayList<>();
        points = new ArrayList<>();

        this.mapbox = _mapbox;

        // Initilizes the MapBox for the MainMapFragment
        // StyleManagement.getCurrentStyle(context)
        mapbox.setStyle(StyleManagement.getCurrentStyle(context), (style) ->
        {
            LocationManagement.enableLocationComponent(context, mapbox, style);

            style.addImage("marker-icon",
                    BitmapFactory.decodeResource(
                            context.getResources(), R.drawable.mapbox_marker_icon_default));
        });

        // Add a point
        mapbox.addOnMapClickListener((point) ->
        {
            // MapBox allows up to 25 points
            if (points.size() < MAX_POINTS)
            {
                // MARKERS management
                UUID newId = MarkerManagement.drawMark(mapbox, Point.fromLngLat(point.getLongitude(), point.getLatitude()));
                ids.add(newId);

                CustomPoint newPoint = new CustomPoint(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
                newPoint.layerId = newId.toString();

                ((MainMapFragment) currentFragment).checkAddRouteVisibility(1, 0);

                return points.add(newPoint);
            }
            else
            {
                Toast.makeText(activity,
                        String.format(Locale.getDefault(),"You can set at least %d points", MAX_POINTS),
                        Toast.LENGTH_SHORT)
                        .show();

                return false;
            }
        });

        // Delete a point
        mapbox.addOnMapLongClickListener((point) ->
        {
            // We delete a mark at point
            // If there is any mark, we will receive its ID; we get null otherwise
            String layerFound = MarkerManagement.deleteMark(mapbox, point, ids);
            if (layerFound != null)
            {
                // We track out points list to remove the attached point to that mark
                if (removePointByLayerId(layerFound))
                {
                    // Updating the add route button
                    ((MainMapFragment) currentFragment).checkAddRouteVisibility(2, 1);

                    Log.d("WeNAVE", "Point removed at at lon={" +
                            point.getLongitude() + "}, lat={" +
                            point.getLatitude() + "}");
                }
                else
                {
                    Log.d("WeNAVE", "No point found at lon={" +
                            point.getLongitude() + "}, lat={" +
                            point.getLatitude() + "}");
                }
            }

            return true;
        });
    }

    /**
     * Pre: there is a point in out list of point which has a mark attached to it with ID layerId
     * Post: the point attached to the mark is deleted
     * @param layerId the Id of the layer
     * @return true if the point was found, false otherwise
     */
    private boolean removePointByLayerId(String layerId)
    {
        for (CustomPoint p : points)
        {
            if (p.layerId.equals(layerId))
            {
                points.remove(p);
                return true;
            }
        }

        return false;
    }

    // Initializes the MapBox for the RouteInfoFragment
    public void setUpMapBoxRoute(final MainActivity context,
                                 MapboxMap _mapbox,
                                 final MapView mapView,
                                 RouteInfoFragment fragment)
    {
        Route route = fragment.route;
        RecyclerView rv = fragment.rv;

        this.mapbox = _mapbox;

        mapbox.setStyle(StyleManagement.getCurrentStyle(context), style ->
        {
            DataAccessLayer.setUpDb(null);
            DataAccessLayer.beginTransaction();

            style.addImage("marker-icon",
                    BitmapFactory.decodeResource(
                            context.getResources(), R.drawable.mapbox_marker_icon_default));

            List<RoutePoint> list = route.getRoutePoints();
            MarkerManagement.drawManyMarks(style, list);

            MapBoxManagement.this.showRoute(context, mapView, route);

            RVAdapter adapter = new RVAdapter(route.getRoutePoints(), context, MapBoxManagement.this);
            rv.setAdapter(adapter);

            DataAccessLayer.commitTransaction();
        });

        Point startLocation = ((RoutePoint)DataAccessLayer
                .getRoutePointsByRoute(route.getId())
                .get(0))
                .getPoint();

        CameraManagement.animateCamera(mapbox, startLocation, 1000);
    }

    private void showRoute(Context context, MapView mapView, Route route)
    {
        List<RoutePoint> points = route.getRoutePoints();

        NavigationRoute.Builder builder = NavigationRoute.builder(context.getApplicationContext())
                .accessToken(TOKEN)
                .origin(points.get(0).getPoint())
                .profile(Enums.getCriteriaByString(route.getOption()))
                .destination(points.get(points.size() - 1).getPoint());

        for (int i = 1; i < points.size() - 1; ++i)
        {
            builder.addWaypoint(points.get(i).getPoint());
        }

        // Route fetched from NavigationRoute
        builder.build().getRoute(new Callback<DirectionsResponse>()
        {
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response)
            {
                assert response.body() != null;
                DirectionsRoute route = response.body().routes().get(0);
                // Draw the route on the map
                if (navigationMapRoute != null)
                {
                    navigationMapRoute.removeRoute();
                }
                else
                {
                    navigationMapRoute = new NavigationMapRoute(null, mapView, mapbox);//, R.style.NavigationMapRoute);
                }

                navigationMapRoute.addRoute(route);
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t)
            {
                // do stuff
            }
        });
    }

    public void addRoute(final MainActivity activity)
    {
        // We retrieve the durations between points
        // in a callback. Then we must generate the route using those durations
        MapBoxService.getDurations(activity, points, durations ->
        {
            MapBoxService.getRoute(activity, points, durations, time == null ? new Date() : time);
        });
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public void clearPoints()
    {
        Style style = mapbox.getStyle();

        if (style != null)
        {
            for (UUID id : ids)
            {
                style.removeLayer(id.toString());
            }
        }

        ((MainMapFragment)currentFragment).setAddRouteVisibility(View.INVISIBLE);
        this.points.clear();
    }

    public void fitPointsAsMode(Enums.Mode currentMode)
    {
        Location userLocation = activity.usersLocation;
        CustomPoint startPoint = new CustomPoint(Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude()));
        if (currentMode == CIRCUIT_MODE)
        {
            points.add(0, startPoint);
            points.add(startPoint);
        }
        else if (currentMode == LOCATION_MODE)
        {
            points.add(0, startPoint);
        }
    }
}