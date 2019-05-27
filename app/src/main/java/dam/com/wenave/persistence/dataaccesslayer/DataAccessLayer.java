package dam.com.wenave.persistence.dataaccesslayer;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.Forecast;
import dam.com.wenave.models.Route;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.RealmRepository;
import dam.com.wenave.persistence.interfaces.IRealmSpecification;
import dam.com.wenave.persistence.specifications.RoutePointSpecification;
import dam.com.wenave.persistence.specifications.RouteSpecification;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DataAccessLayer
{
    private static RealmRepository db;
    public static boolean isInTransaction = false;

    public static void setUpDb(RealmConfiguration rconf)
    {
        db = new RealmRepository(rconf);
    }

    public static List<? extends RealmObject> getRoutePointsByRoute(String routeId)
    {
        IRealmSpecification spec = new RoutePointSpecification(routeId);
        return db.query(spec);
    }

    public static Route getRouteById(String id)
    {
        return (Route) db.query(new RouteSpecification(id)).get(0);
    }

    public static List<? extends RealmObject> getRoutes()
    {
        return db.query(new RouteSpecification(null));
    }

    public static void addRoute (Route route)
    {
        addRoutePoints(route.getRoutePoints());
        db.add(route);
    }

    public static void addRoutePoints (List<RoutePoint> routePoint)
    {
        for (RoutePoint rp : routePoint)
        {
            db.add(rp.getCustomPoint());
            db.add(rp.getForecast());
            db.add(rp);
        }
    }

    public static Route createRoute()
    {
        return db.createRoute();
    }

    public static RoutePoint createRoutePoint()
    {
        return db.createRoutePoint();
    }

    public static Forecast createForecast()
    {
        return db.createForecast();
    }

    public static CustomPoint createPoint()
    {
        return db.createPoint();
    }

    public static void beginTransaction()
    {
        if (! isInTransaction)
        {
            db.beginTransaction();
            isInTransaction = !isInTransaction;
        }
    }

    public static void commitTransaction()
    {
        if (isInTransaction)
        {
            db.commitTransaction();
            isInTransaction = !isInTransaction;
        }
    }

    public static void deleteRoute (Route route)
    {
        beginTransaction();

        List<? extends RealmObject> routePoints = getRoutePointsByRoute(route.getId());

        Iterator<?  extends RealmObject> it = routePoints.iterator();

        while (it.hasNext())
        {
            RoutePoint rP = (RoutePoint) it.next();
            db.remove(db.getForecastAsResults(rP.getForecast()));
            db.remove(db.getCustomPointAsResults(rP.getCustomPoint()));
            db.remove(db.getRoutePointAsResults(rP));
        }

        db.remove(db.getRouteAsResults(route));
        commitTransaction();
    }

    public static void deleteRoutes()
    {
        RealmResults<Route> results = db.getRoutes();
        db.remove(results);
    }

    public static void updateForecast(Forecast f)
    {
        db.update(f);
    }

    public static void updateRoutePoint(RoutePoint routePoint)
    {
        db.update(routePoint);
    }

    public static void updateRoute(Route route)
    {
        db.update(route);
    }

    // In order to prevent threading connection channel conflicts
    // We must wrap our realm object into a volatile closable layer
    public static class EmbeddedDataAccessLayer implements Closeable
    {
        RealmRepository embeddedDb;
        public EmbeddedDataAccessLayer()
        {
            embeddedDb = new RealmRepository(null);
        }

        public List<RealmObject> getRoutePointsByRoute(Route route)
        {
            IRealmSpecification spec = new RoutePointSpecification(route.getId());
            return embeddedDb.query(spec);
        }

        public Route getRouteById(String id)
        {
            return (Route) embeddedDb.query(new RouteSpecification(id)).get(0);
        }

        public List<RealmObject> getRoutes()
        {
            return embeddedDb.query(new RouteSpecification(null));
        }

        public void addRoute (Route route)
        {
            addRoutePoints(route.getRoutePoints());
            embeddedDb.add(route);
        }

        public void addRoutePoints (List<RoutePoint> routePoint)
        {
            for (RoutePoint rp : routePoint)
            {
                embeddedDb.add(rp.getCustomPoint());
                embeddedDb.add(rp.getForecast());
                embeddedDb.add(rp);
            }
        }

        public Route createRoute() {
            return embeddedDb.createRoute();
        }

        public RoutePoint createRoutePoint() {
            return embeddedDb.createRoutePoint();
        }

        public Forecast createForecast()
        {
            return embeddedDb.createForecast();
        }

        public CustomPoint createPoint()
        {
            return embeddedDb.createPoint();
        }

        public void beginTransaction()
        {
            embeddedDb.beginTransaction();
        }

        public void commitTransaction()
        {
            embeddedDb.commitTransaction();
        }

        @Override
        public void close()
        {
            db.close();
        }
    }
}
