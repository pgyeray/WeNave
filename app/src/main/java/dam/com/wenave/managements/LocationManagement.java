package dam.com.wenave.managements;

import android.location.Location;
import androidx.annotation.NonNull;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import dam.com.wenave.views.activities.MainActivity;

public class LocationManagement
{
    public static PermissionsManager permissionsManager;

    @SuppressWarnings( {"MissingPermission"})
    public static void enableLocationComponent(MainActivity ctx, MapboxMap mapboxMap, @NonNull Style loadedMapStyle)
    {
        if (PermissionsManager.areLocationPermissionsGranted(ctx))
        {
            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(ctx)
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions
                            .builder(ctx, loadedMapStyle)
                            .locationComponentOptions(locationComponentOptions)
                            .build();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            ctx.usersLocation = locationComponent.getLastKnownLocation();

            //assert ctx.usersLocation != null;
            if (ctx.usersLocation != null)
            {
                CameraManagement.animateCamera(mapboxMap, Point.fromLngLat(ctx.usersLocation.getLongitude(), ctx.usersLocation.getLatitude()), 1000);
            }
        }
        else
        {
            permissionsManager = new PermissionsManager(ctx);
            permissionsManager.requestLocationPermissions(ctx);
        }
    }
}
