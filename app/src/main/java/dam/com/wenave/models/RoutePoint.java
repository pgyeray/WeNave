package dam.com.wenave.models;

import androidx.room.PrimaryKey;

import com.mapbox.geojson.Point;

import java.util.Objects;
import java.util.UUID;

import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import io.realm.RealmObject;

public class RoutePoint extends RealmObject
{
    public static RoutePoint newInstance(CustomPoint p, Forecast f)
    {
        DataAccessLayer.beginTransaction();
        RoutePoint rp = DataAccessLayer.createRoutePoint();

        //rp.id = UUID.randomUUID().toString();
        rp.forecast = f;
        rp.point = p;

        DataAccessLayer.commitTransaction();
        return rp;
    }

    @PrimaryKey
    private String id;

    private CustomPoint point;
    private Forecast forecast;

    public String routeId;

    public RoutePoint()
    {
        this.id = UUID.randomUUID().toString();
    }

    public RoutePoint(Point point, Forecast forecast)
    {
        this();

        this.point = new CustomPoint(point);
        this.forecast = forecast;
    }

    public String getId()
    {
        return id;
    }

    public Point getPoint()
    {
        return point.castToPoint();
    }
    public CustomPoint getCustomPoint()
    {
        return point;
    }

    public Forecast getForecast()
    {
        return forecast;
    }

    public void setForecast(Forecast forecast)
    {
        DataAccessLayer.beginTransaction();
        this.forecast = forecast;
        DataAccessLayer.commitTransaction();
    }

    @Override
    public String toString()
    {
        return "RoutePoint{" +
                "point={" + point.longitude + ", " + point.latitude +
                ", \nforecast=" + forecast.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutePoint that = (RoutePoint) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}