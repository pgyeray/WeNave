package dam.com.wenave.services;

import com.mapbox.geojson.Point;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import dam.com.wenave.models.Forecast;
import dam.com.wenave.services.callbacks.darksky.DarkSkyAPIDownloader;
import dam.com.wenave.services.callbacks.darksky.Parser;

public class DarkSkyService
{
    static
    {
        System.loadLibrary("keys");
    }

    public static native String getDarkskyUrl();
    private static String MAIN_URL = getDarkskyUrl() + Locale.getDefault().getLanguage();

    public static Forecast getForecast(Point point, Long millis)
    {
        try
        {
            URL url = getUrl(point, millis);
            String forecastRaw = new DarkSkyAPIDownloader().execute(url);
            return new Parser().execute(forecastRaw);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static URL getUrl(Point point, Long millis) throws MalformedURLException
    {
        String url = MAIN_URL.replace("lat", String.valueOf(point.latitude()));
        url = url.replace("lon", String.valueOf(point.longitude()));
        url = url.replace("mil", String.valueOf(millis / 1000));

        return new URL(url);
    }



}
