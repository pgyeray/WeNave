package dam.com.wenave.managements;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.maps.Style;
import java.util.HashMap;

import dam.com.wenave.R;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.views.fragments.MainMapFragment;

public class StyleManagement
{
    private static final String STYLES_LIST_KEY = "style_list";
    private static final HashMap<String, String> STYLES;
    private static int currentLabelColor;

    static
    {
        STYLES = new HashMap<>(8);
        STYLES.put("Dark", Style.DARK);
        STYLES.put("Light", Style.LIGHT);
        STYLES.put("Mapbox Streets", Style.MAPBOX_STREETS);
        STYLES.put("Outdoors", Style.OUTDOORS);
        STYLES.put("Satellite Streets", Style.SATELLITE_STREETS);
        STYLES.put("Traffic Day", Style.TRAFFIC_DAY);
        STYLES.put("Traffic night", Style.TRAFFIC_NIGHT);
    }

    static String getCurrentStyle (MainActivity ctx)
    {
        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String styleKey = prefs.getString(STYLES_LIST_KEY, "Traffic night"); // check if-null

            if (ctx.currentFragment instanceof MainMapFragment)
            {
                setLabelsColors(styleKey, ctx);
            }

            return STYLES.get(styleKey);
        }
        catch (Exception e)
        {
            Toast.makeText(ctx, "Error loading style", Toast.LENGTH_SHORT).show();
            Log.e("WeNAVE", "[x] Error loading style " + e.getMessage());
            return null;
        }
    }

    private static void setLabelsColors(String styleKey, MainActivity ctx)
    {
        TextView normalLabel = (TextView) ctx.findViewById(R.id.normal_mode_hint);
        TextView locationLabel = (TextView) ctx.findViewById(R.id.location_mode_hint);
        TextView circuitLabel = (TextView) ctx.findViewById(R.id.circuit_mode_hint);

        switch (styleKey)
        {
            case "Dark":
            case "Traffic night":
            case "Satellite":
            case "Satellite Streets":
                int idColor = ctx.getResources().getColor(R.color.buttons_background);

                normalLabel.setTextColor(idColor);
                locationLabel.setTextColor(idColor);
                circuitLabel.setTextColor(idColor);

                currentLabelColor = idColor;

                break;

            // Light
            // MapBox Streets
            // Outdoors
            // Traffic Day
            default:
                idColor = ctx.getResources().getColor(R.color.colorPrimaryDark);

                normalLabel.setTextColor(idColor);
                locationLabel.setTextColor(idColor);
                circuitLabel.setTextColor(idColor);

                currentLabelColor = idColor;
        }

        int selectedLabelColorId = ctx.getResources().getColor(R.color.mapbox_blue);
        // currentFragment instanceof MainMapFragment is true
        switch (((MainMapFragment)ctx.currentFragment).currentMode)
        {
            case NORMAL_MODE:
                normalLabel.setTextColor(selectedLabelColorId);
                break;
            case LOCATION_MODE:
                locationLabel.setTextColor(selectedLabelColorId);
                break;
            case CIRCUIT_MODE:
                circuitLabel.setTextColor(selectedLabelColorId);
        }
    }

    public static int getCurrentLabelColor()
    {
        return currentLabelColor;
    }

}
