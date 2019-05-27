package dam.com.wenave.services.callbacks.darksky;

import java.net.URL;
import java.net.HttpURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.io.IOException;

public class DarkSkyAPIDownloader
{

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public String execute (URL url)
    {
        String result = "";
        String inputLine;
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            InputStreamReader streamReader
                    = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputLine = reader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();
            connection.disconnect();
            result = stringBuilder.toString();

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
}