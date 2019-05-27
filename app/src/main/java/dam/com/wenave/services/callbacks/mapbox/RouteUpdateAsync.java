package dam.com.wenave.services.callbacks.mapbox;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.mapbox.geojson.Point;

import java.util.Date;
import java.util.Iterator;

import dam.com.wenave.R;
import dam.com.wenave.models.Forecast;
import dam.com.wenave.models.Route;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.services.DarkSkyService;
import dam.com.wenave.services.interfaces.IOnRouteUpdated;
import dam.com.wenave.views.activities.MainActivity;
import io.realm.RealmList;

public class RouteUpdateAsync extends AsyncTask <String, Void, String>
{
    private ProgressBar progressBar;
    private MainActivity context;
    private IOnRouteUpdated callback;
    private Date newDate;

    public RouteUpdateAsync(MainActivity context, IOnRouteUpdated callback, Date newDate)
    {
        this.context = context; // Access to progressBar
        this.callback = callback; // Where will we launch the response
        this.newDate = newDate; // New date to start the route: null if undefined
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressBar = (ProgressBar) context.findViewById(R.id.creatingRouteProgress);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... routes)
    {
        // We must reset Realm channel connection because of threading
        DataAccessLayer.setUpDb(null);
        Route route = DataAccessLayer.getRouteById(routes[0]);

        Date now = new Date();
        // When the route starts
        Date firstTime = newDate == null ? route.getRoutePoints().get(0).getForecast().getDate() : newDate;

        // We will recalculate times when the route starts before now
        boolean mustRecalculateTimes = newDate != null || firstTime.before(now);

        // The list where updated RoutePoints will be added
        RealmList<RoutePoint> newRoutePoints = new RealmList<>();
        Iterator<RoutePoint> it = route.getRoutePoints().iterator();

        // We must persist a sentinel due to duration calculation
        RoutePoint previous = null;

        // TimeInMillis is the previous Date requested to DarkSky and will be updated to the next,
        // adding the difference between RoutePoints: the duration.
        // Initialized with 0 due to compilation errors
        long timeInMillis = 0;

        do
        {
            RoutePoint routePoint = it.next();
            Point point = routePoint.getPoint();

            if (mustRecalculateTimes)
            {
                // First RoutePoint
                if (routePoint.equals(route.getRoutePoints().get(0)))
                {
                    // Chance between the new date to update or "now"
                    timeInMillis = firstTime.before(now) ? now.getTime() : firstTime.getTime();
                }
                // Recalculate duration
                else
                {
                    // Difference between the previuos point and the current one
                    long duration = routePoint.getForecast().getDate().getTime() - previous.getForecast().getDate().getTime();
                    timeInMillis = (timeInMillis + duration); // updating the request time
                }
            }
            // If there's no need to recalculate times, we just pìck the original times
            else
            {
                timeInMillis = routePoint.getForecast().getDate().getTime();
            }

            // DarkSky API request
            Forecast newForecast = DarkSkyService.getForecast(point, timeInMillis);

            // Forecast & RoutePoint updating
            newForecast = Forecast.newInstance(newForecast);
            newForecast.setId(routePoint.getForecast().getId());
            DataAccessLayer.updateForecast(newForecast);

            routePoint.setForecast(newForecast);

            DataAccessLayer.updateRoutePoint(routePoint);

            newRoutePoints.add(routePoint);

            previous = routePoint;
        } while (it.hasNext());

        // Route updating
        route.setRoutePoints(newRoutePoints);
        route.setModifiedDate(new Date());
        DataAccessLayer.updateRoute(route);

        return route.getId();
    }

    @Override
    protected void onPostExecute(String routeId)
    {
        super.onPostExecute(routeId);

        progressBar.setVisibility(View.INVISIBLE);
        callback.onRouteUpdated(routeId);
    }
}
