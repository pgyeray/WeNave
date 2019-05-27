package dam.com.wenave.services.interfaces;

import dam.com.wenave.models.Route;

public interface IFragmentLaucher {
    void launchRouteFragment (String route);
    void launchExistingRoute (Route route);
}
