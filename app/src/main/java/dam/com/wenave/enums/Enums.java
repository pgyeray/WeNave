package dam.com.wenave.enums;

import com.mapbox.api.directions.v5.DirectionsCriteria;

public class Enums
{
    public enum Mode
    {
        NORMAL_MODE,
        CIRCUIT_MODE,
        LOCATION_MODE
    }

    public enum RouteOption
    {
        DRIVING,
        CYCLING,
        RUNNING
    }

    public static String getCriteriaByOption(RouteOption option)
    {
        switch (option)
        {
            case DRIVING:
                return DirectionsCriteria.PROFILE_DRIVING;
            case CYCLING:
                return DirectionsCriteria.PROFILE_CYCLING;
            case RUNNING:
                return DirectionsCriteria.PROFILE_WALKING;
            default:
                return null;
        }
    }

    public static RouteOption getOptionByString(String option)
    {
        switch (option)
        {
            case "Cycling":
                return RouteOption.CYCLING;
            case "Running":
                return RouteOption.RUNNING;
            default:
                return RouteOption.DRIVING;
        }
    }

    public static String getCriteriaByString (String option)
    {
        switch (option)
        {
            case "Cycling":
                return DirectionsCriteria.PROFILE_CYCLING;
            case "Running":
                return DirectionsCriteria.PROFILE_WALKING;
            default:
                return DirectionsCriteria.PROFILE_DRIVING;
        }
    }
}
