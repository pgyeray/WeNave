package dam.com.wenave.services.callbacks.mapbox;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.mapbox.geojson.Point;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import dam.com.wenave.R;
import dam.com.wenave.enums.Enums;
import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.Forecast;
import dam.com.wenave.models.Route;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.services.DarkSkyService;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.views.fragments.PreferenceFragment;
import io.realm.RealmList;

public class RouteGeneratorAsync extends AsyncTask<Pair<List<CustomPoint>,List<Double>>, Void, String>
{
    private ProgressBar progressBar;
    private Date startTime;
    private MainActivity context;

    public RouteGeneratorAsync(MainActivity context, Date startTime)
    {
        this.context = context;
        this.startTime = startTime;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressBar = (ProgressBar) context.findViewById(R.id.creatingRouteProgress);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Pair<List<CustomPoint>, List<Double>>... pairs)
    {
        DataAccessLayer.setUpDb(null);
        //DataAccessLayer.EmbeddedDataAccessLayer realm = new DataAccessLayer.EmbeddedDataAccessLayer();
        List<CustomPoint> points = pairs[0].first;
        List<Double> durations = pairs[0].second;

        RealmList<RoutePoint> routePoints = new RealmList<>();

        long millis = startTime.getTime();

        Iterator<CustomPoint> itPoints = points.iterator();
        Iterator<Double> itDurations = durations.iterator();
        do
        {
            Point p = itPoints.next().castToPoint();
            Forecast f = DarkSkyService.getForecast(p, millis);

            assert f != null;
            Forecast forecast = Forecast.newInstance(f);
            CustomPoint customPoint = CustomPoint.newInstance(p);

            routePoints.add(RoutePoint.newInstance(customPoint, forecast));

            millis += itDurations.hasNext() ? itDurations.next().longValue() * 1000 : 0; // null-checked
        }
        while (itPoints.hasNext());

        String option = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("route_options", "Driving"); // check if-null

        Route r = Route.newInstance(routePoints, option);

        DataAccessLayer.addRoute(r);

        return r.getId();
    }

    @Override
    protected void onPostExecute(String routeId)
    {
        super.onPostExecute(routeId);

        progressBar.setVisibility(View.INVISIBLE);
        context.launchRouteFragment(routeId);
    }
}