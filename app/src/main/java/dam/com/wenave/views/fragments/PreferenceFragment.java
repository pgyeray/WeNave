package dam.com.wenave.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import dam.com.wenave.R;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.views.activities.MainActivity;

public class PreferenceFragment extends PreferenceFragmentCompat
{
    @SuppressLint("LogNotTimber")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        Preference mapStylePref = (Preference) findPreference("style_list");

        // Whenever the user changes the style, the previous fragment will be charged
        mapStylePref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                MainActivity activity = ((MainActivity) getActivity());

                if (activity == null)
                {
                    throw new NullPointerException();
                }

                if (activity.currentFragment instanceof RouteInfoFragment)
                {
                    activity.changeFragment(RouteInfoFragment.class,
                            DataAccessLayer.getRouteById(((RouteInfoFragment) activity.currentFragment).route.getId()),
                            ((RouteInfoFragment) activity.currentFragment).isExisting());

                    return true;
                }

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main, activity.currentFragment)
                        .commit();

                return true;
            }
            catch (Exception e)
            {
                Log.e("WeNAVE", "MainActivity not defined\n" + e.getMessage());
                return false;
            }
        });

        Preference optionsPref = (Preference) findPreference("route_options");
        optionsPref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                MainActivity activity = ((MainActivity) getActivity());

                if (activity == null)
                {
                    throw new NullPointerException();
                }

                if (activity.currentFragment instanceof MainMapFragment)
                {
                    ((MainMapFragment)(activity.currentFragment)).setOptionLabel();
                }

                return true;
            }
            catch (Exception e)
            {
                Log.e("WeNAVE", "MainActivity not defined\n" + e.getMessage());
                return false;
            }
        });
    }
}