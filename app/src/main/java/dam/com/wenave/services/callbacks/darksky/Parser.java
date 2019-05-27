package dam.com.wenave.services.callbacks.darksky;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import dam.com.wenave.models.Forecast;


public class Parser
{

    public Forecast execute(String forecastRaw)
    {
        try
        {
            JSONObject currently = (JSONObject) new JSONObject(forecastRaw).get("currently");

            return new Forecast(currently.getString("icon"),
                    new Date(currently.getLong("time") * 1000),
                    currently.getDouble("temperature"),
                    currently.getDouble("precipProbability"),
                    currently.getDouble("humidity"),
                    currently.getString("summary"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}




