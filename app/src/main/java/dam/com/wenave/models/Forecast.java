package dam.com.wenave.models;

import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import io.realm.RealmObject;

public class Forecast extends RealmObject implements Serializable
{
    public static Forecast newInstance(String icon, Date date, Double temperature, Double precipitation, Double humidity, String summary)
    {
        DataAccessLayer.beginTransaction();

        Forecast f = DataAccessLayer.createForecast();

        //f.id = UUID.randomUUID().toString();
        f.icon = icon;
        f.date = date;
        f.temperature = temperature;
        f.precipitation = precipitation;
        f.humidity = humidity;
        f.summary = summary;

        DataAccessLayer.commitTransaction();

        return f;
    }

    public static Forecast newInstance(Forecast forecast)
    {
        DataAccessLayer.beginTransaction();

        Forecast f = DataAccessLayer.createForecast();

        //f.id = UUID.randomUUID().toString();
        f.icon = forecast.icon;
        f.date = forecast.date;
        f.temperature = forecast.temperature;
        f.precipitation = forecast.precipitation;
        f.humidity = forecast.humidity;
        f.summary = forecast.summary;

        DataAccessLayer.commitTransaction();

        return f;
    }

    @PrimaryKey
    private String id;

    private String icon;
    private Date date;
    private Double temperature;
    private Double precipitation;
    private Double humidity;
    private String summary;

    public Forecast()
    {
        this.id = UUID.randomUUID().toString();
    }

    public Forecast(String icon, Date date, Double temperature, Double precipitation, Double humidity, String summary)
    {
        this();

        this.icon = icon;
        this.date = date;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.summary = summary;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        DataAccessLayer.beginTransaction();
        this.id = id;
        DataAccessLayer.commitTransaction();
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        DataAccessLayer.beginTransaction();
        this.date = date;
        DataAccessLayer.commitTransaction();
    }

    public Double getTemperature()
    {
        return temperature;
    }

    public void setTemperature(Double temperature)
    {
        this.temperature = temperature;
    }

    public Double getPrecipitation()
    {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation)
    {
        this.precipitation = precipitation;
    }

    public Double getHumidity()
    {
        return humidity;
    }

    public void setHumidity(Double humidity)
    {
        this.humidity = humidity;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    @Override
    public String toString()
    {
        return "Forecast{" +
                "icon='" + icon + '\'' +
                ", date=" + date +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", humidity=" + humidity +
                ", summary='" + summary + '\'' +
                '}';
    }

}
