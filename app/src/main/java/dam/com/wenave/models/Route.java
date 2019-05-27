package dam.com.wenave.models;

import androidx.room.PrimaryKey;

import com.mapbox.geojson.Point;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dam.com.wenave.enums.Enums;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Route extends RealmObject implements Serializable
{
    public static Route newInstance(RealmList<RoutePoint> routePoints, String option)
    {
        DataAccessLayer.beginTransaction();
        Route r = DataAccessLayer.createRoute();

        //r.id = UUID.randomUUID().toString();
        r.routePoints = routePoints;

        for (RoutePoint rp : r.routePoints)
        {
            rp.routeId = r.id;
        }

        r.creationDate = new Date();
        r.modifiedDate = new Date();
        r.option = option;

        DataAccessLayer.commitTransaction();
        return r;
    }

    @PrimaryKey
    private String id;
    public String routeTitle = "";

    private Date creationDate;
    private Date modifiedDate;

    private String option;

    private RealmList<RoutePoint> routePoints = new RealmList<>();

    public Route()
    {
        this.id = UUID.randomUUID().toString();
        creationDate = new Date();
        modifiedDate = new Date();
    }

    public Route(UUID id)
    {
        this.id = id.toString();
        creationDate = new Date();
        modifiedDate = new Date();
    }

    public boolean addRoutePoint(Point p, Forecast f)
    {
        RoutePoint rP = new RoutePoint(p, f);
        return routePoints.add(rP);
    }

    public List<RoutePoint> getRoutePoints()
    {
        return routePoints;
    }

    public void setRoutePoints(RealmList<RoutePoint> routePoints)
    {
        DataAccessLayer.beginTransaction();
        this.routePoints = routePoints;
        DataAccessLayer.commitTransaction();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getModifiedDate()
    {
        return modifiedDate;
    }

    public String getOption()
    {
        return option;
    }

    public void setModifiedDate(Date modifiedDate)
    {
        DataAccessLayer.beginTransaction();
        this.modifiedDate = modifiedDate;
        DataAccessLayer.commitTransaction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return id.equals(route.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Avoiding SPAM
     * startRoute >= lastModified
     * Now >= lastModified
     *
     * @param route
     * @return the minutes left to update again the route, n where n < 0 if the route allows to update itself, n >= 0 otherwise
     */
    public static long requestModification (Route route, Date newDate)
    {
        Date now = new Date();
        Date startRoute = newDate == null ? route.getRoutePoints().get(0).getForecast().getDate() : newDate;
        Date lastModified = route.getModifiedDate();

        long diffStartModifiedMillisInMinutes = (startRoute.getTime() - lastModified.getTime()) / 1000 / 60;
        long diffModifiedNowInMinutes = (now.getTime() - lastModified.getTime()) / 1000 / 60;

        if (diffModifiedNowInMinutes <= 30) // 0.5 hours
        {
            return (30 - diffModifiedNowInMinutes); // never
        }

        // diff >= 30min
        if (diffModifiedNowInMinutes <= 60) // 1 hours
        {
            return diffStartModifiedMillisInMinutes <= 720 ? -1 : (60 - diffModifiedNowInMinutes); // 12 hours
        }

        // diff >= 120
        if (diffModifiedNowInMinutes <= 90) // 1.5 hours
        {
            return diffStartModifiedMillisInMinutes <= 1440 ? -1 : (90 - diffModifiedNowInMinutes); // 24 hours
        }

        // always
        return -1;
    }

    private static long map (long value, long istart, long istop, long ostart, long ostop)
    {
        if (value <= istart)
        {
            return ostart;
        }
        if (value >= istop)
        {
            return ostop;
        }
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }
}
