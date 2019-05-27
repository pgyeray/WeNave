package dam.com.wenave.persistence;

import java.util.ArrayList;
import java.util.List;

import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.Forecast;
import dam.com.wenave.models.Route;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.persistence.interfaces.IRealmSpecification;
import dam.com.wenave.persistence.interfaces.IRepository;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmRepository implements IRepository<RealmObject>
{
    private RealmConfiguration realmConf;
    private Realm realm;

    public RealmRepository(RealmConfiguration realmConf)
    {
        this.realmConf = realmConf;
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void add(RealmObject item)
    {
        if (DataAccessLayer.isInTransaction)
        {
            realm.copyToRealm(item);
        }
        else
        {
            realm.executeTransaction(realm -> realm.copyToRealm(item));
        }
    }

    @Override
    public void add(Iterable<RealmObject> items)
    {
        if (DataAccessLayer.isInTransaction)
        {
            realm.copyToRealm(items);
        }
        else
        {
            realm.executeTransaction(realm -> realm.copyToRealm(items));
        }
    }

    @Override
    public void update(RealmObject item)
    {
        if (DataAccessLayer.isInTransaction)
        {
            realm.copyToRealm(item);
        }
        else
        {
            realm.executeTransaction(realm -> realm.copyToRealm(item));
        }
    }

    @Override
    public void remove(RealmResults<? extends RealmObject> items)
    {
        if (DataAccessLayer.isInTransaction)
        {
            items.deleteAllFromRealm();
        }
        else
        {
            realm.executeTransaction(realm ->
            {
                items.deleteAllFromRealm();
            });
        }

        // TODO close?
    }

    @Override
    public List<RealmObject> query(IRealmSpecification specification)
    {
        RealmResults<? extends RealmObject> results = specification.toRealmResults(realm);
        return new ArrayList<>(results);
    }

    @Override
    public Route createRoute()
    {
        return realm.createObject(Route.class);
    }

    @Override
    public RoutePoint createRoutePoint()
    {
        return realm.createObject(RoutePoint.class);
    }

    @Override
    public Forecast createForecast()
    {
        return realm.createObject(Forecast.class);
    }

    @Override
    public CustomPoint createPoint()
    {
        return realm.createObject(CustomPoint.class);
    }

    @Override
    public RealmResults<?  extends RealmObject> getForecastAsResults (Forecast f)
    {
        return realm.where(Forecast.class).equalTo("id", f.getId()).findAll();
    }

    @Override
    public RealmResults<? extends RealmObject> getCustomPointAsResults (CustomPoint c)
    {
        return realm.where(CustomPoint.class).equalTo("id", c.getId()).findAll();
    }

    @Override
    public RealmResults<? extends RealmObject> getRoutePointAsResults (RoutePoint rP)
    {
        return realm.where(RoutePoint.class).equalTo("id", rP.getId()).findAll();
    }

    @Override
    public RealmResults<? extends RealmObject> getRouteAsResults (Route r)
    {
        return realm.where(Route.class).equalTo("id", r.getId()).findAll();
    }

    @Override
    public void beginTransaction()
    {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
    }

    @Override
    public void commitTransaction()
    {
        realm.commitTransaction();
    }

    @Override
    public void close()
    {
        realm.close();
    }

    @Override
    public RealmResults<Route> getRoutes() {
        return realm.where(Route.class).findAll();
    }
}
