package dam.com.wenave.services;

import android.app.Activity;
import android.util.Pair;

import com.mapbox.geojson.Point;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.services.callbacks.mapbox.RouteGeneratorAsync;
import dam.com.wenave.services.callbacks.mapbox.RouteUpdateAsync;
import dam.com.wenave.services.interfaces.IOnRouteUpdated;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.services.callbacks.mapbox.DurationGetterAsync;
import dam.com.wenave.services.interfaces.IMapBoxResponse;

public class MapBoxService
{
    public static void getDurations (Activity activity, List<CustomPoint> points, IMapBoxResponse callback)
    {
        DurationGetterAsync request = new DurationGetterAsync(activity, callback);
        request.execute(points);
    }

    public static void getRoute(MainActivity context, List<CustomPoint> points, List<Double> durations, Date startTime)
    {
        //MapBoxService.context = context;
        new RouteGeneratorAsync(context, startTime).execute(new Pair<List<CustomPoint>, List<Double>>(points, durations));
    }

    public static void getUpdatedRoute (MainActivity context, String routeId, Date newDate, IOnRouteUpdated callback)
    {
        new RouteUpdateAsync(context, callback, newDate).execute(routeId);
    }
}
