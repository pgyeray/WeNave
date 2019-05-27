package dam.com.wenave.persistence.interfaces;

import java.util.List;

import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.Forecast;
import dam.com.wenave.models.Route;
import dam.com.wenave.models.RoutePoint;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface IRepository<T>
{
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(RealmResults<? extends RealmObject> item);

    List<T> query(IRealmSpecification specification);

    Route createRoute();
    RoutePoint createRoutePoint();
    Forecast createForecast();
    CustomPoint createPoint();

    RealmResults<?  extends RealmObject> getForecastAsResults (Forecast c);
    RealmResults<?  extends RealmObject> getCustomPointAsResults (CustomPoint c);
    RealmResults<?  extends RealmObject> getRoutePointAsResults (RoutePoint c);
    RealmResults<?  extends RealmObject> getRouteAsResults (Route c);

    void beginTransaction();

    void commitTransaction();

    void close();

    RealmResults<Route> getRoutes();
}
