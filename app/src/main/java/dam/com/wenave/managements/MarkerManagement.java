package dam.com.wenave.managements;

import android.graphics.PointF;
import androidx.annotation.NonNull;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.RoutePoint;

class MarkerManagement
{
    private static UUID drawMark(Style style, Point point)
    {
        // Every mark is defined with a single UUID, its resources have the same UUID
        // Whenever we insert a mark, we will return the assigned id
        UUID uuid = UUID.randomUUID();

        Feature f = Feature.fromGeometry(point);
        ArrayList<Feature> mark = new ArrayList<>();
        mark.add(f);
        style.addSource(new GeoJsonSource(uuid.toString(),
                FeatureCollection.fromFeatures(mark)));

        SymbolLayer symbolLayer = new SymbolLayer(uuid.toString(), uuid.toString());
        symbolLayer.withProperties(
                PropertyFactory.iconImage("marker-icon")
        );

        style.addLayer(symbolLayer);

        return uuid;
    }

    private static UUID drawMark(Style style, CustomPoint point)
    {
        UUID id = drawMark(style, point.castToPoint());
        point.layerId = id.toString();
        return id;
    }

    static UUID drawMark(MapboxMap mapBox, Point point)
    {
        return drawMark(mapBox.getStyle(), point);
    }

    static List<UUID> drawManyMarks(Style style, List<RoutePoint> points)
    {
        LinkedList<UUID> ids = new LinkedList<>();

        for (RoutePoint p : points)
        {
            ids.add(MarkerManagement.drawMark(style, p.getCustomPoint()));
        }

        return ids;
    }

    static String deleteMark(@NonNull MapboxMap mapBox, LatLng point, @NonNull List<UUID> ids)
    {
        Style style = mapBox.getStyle();
        boolean removed = false;
        if (style != null)
        {
            String layerId = "null@layerId";
            for (UUID id : ids)
            {
                layerId = id.toString();
                final PointF pixel = mapBox.getProjection().toScreenLocation(point);
                List<Feature> features = mapBox.queryRenderedFeatures(pixel, layerId);

                if (features.size() > 0)
                {
                    style.removeLayer(layerId);
                    style.removeSource(layerId);

                    removed = true;
                    break;
                }
            }
            Log.d("WeNAVE", removed ? "Layer " + layerId + " removed successfully" : "No layer found");
            return layerId;
        }

        Log.e("WeNAVE", "MapBox style is null");
        return null;
    }
}
