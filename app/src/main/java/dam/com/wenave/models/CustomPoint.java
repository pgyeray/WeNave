package dam.com.wenave.models;

import androidx.room.PrimaryKey;

import com.mapbox.geojson.Point;

import java.util.UUID;

import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import io.realm.RealmObject;

public class CustomPoint extends RealmObject
{
    public static CustomPoint newInstance(Point p)
    {
        DataAccessLayer.beginTransaction();
        CustomPoint o = DataAccessLayer.createPoint();

        //o.id = UUID.randomUUID().toString();
        o.latitude = p.latitude();
        o.longitude = p.longitude();

        DataAccessLayer.commitTransaction();
        return o;
    }

    @PrimaryKey
    public String id;

    public double longitude;
    public double latitude;

    public String layerId;

    public CustomPoint()
    {
        this.id = UUID.randomUUID().toString();
    }

    public CustomPoint(Point p)
    {
        this();
        this.latitude = p.latitude();
        this.longitude = p.longitude();
    }

    public Point castToPoint(){
        return Point.fromLngLat(this.longitude, this.latitude);
    }

    public String getId()
    {
        return id;
    }
}
