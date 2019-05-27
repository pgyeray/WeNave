package dam.com.wenave.managements;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class CameraManagement
{
    public static void animateCamera (MapboxMap mapbox, Point point, int duration)
    {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(point.latitude(), point.longitude()))
                .zoom(10)
                .tilt(20)
                .build();

        mapbox.animateCamera(CameraUpdateFactory.newCameraPosition(position), duration);
    }
}
