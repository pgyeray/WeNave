package dam.com.wenave.services.callbacks.mapbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dam.com.wenave.enums.Enums;
import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.services.interfaces.IMapBoxResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DurationGetterAsync extends AsyncTask<List<CustomPoint>, Void, Void>
{
    static {
        System.loadLibrary("keys");
    }

    public static native String getMapboxUrl();

    private final static String TOKEN = getMapboxUrl();

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private List<Double> durations = new ArrayList<>();
    private IMapBoxResponse callResponse;

    public DurationGetterAsync(Activity activity, IMapBoxResponse response)
    {
        this.activity = activity;
        this.callResponse = response;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<CustomPoint>... points)
    {
        String option = PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getString("route_options", "Driving"); // check if-null
        String criteriaOption = Enums.getCriteriaByString(option);

        Iterator<CustomPoint> it = points[0].iterator();
        Point origin = it.next().castToPoint();

        while(it.hasNext())
        {
            Point destination = it.next().castToPoint();

            NavigationRoute.Builder builder = NavigationRoute.builder(activity.getApplicationContext())
                    .accessToken(TOKEN)
                    .origin(origin)
                    .profile(criteriaOption)
                    .destination(destination);

            builder.build().getRoute(new Callback<DirectionsResponse>()
            {
                @Override
                public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response)
                {
                    assert response.body() != null;
                    DirectionsRoute route = response.body().routes().get(0);
                    durations.add(route.duration());

                    if (durations.size() == points[0].size() -1)
                    {
                        callResponse.getDurationList(durations);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) { }
            });

            origin = destination;
        }

        return null;
    }
}
