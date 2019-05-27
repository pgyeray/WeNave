package dam.com.wenave.views.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

import dam.com.wenave.R;
import dam.com.wenave.views.adapters.UpdateSelectionAdapter;

public class DialogUpdateSelection extends DialogFragment
{
    private RouteInfoFragment infoFragment;

    DialogUpdateSelection(RouteInfoFragment infoFragment)
    {
        this.infoFragment = infoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        String[] options =
        {
                "Refresh now",
                "Keep starting date" ,
                "Select a date"
        };

        Date firstTime = infoFragment
                .route
                .getRoutePoints()
                .get(0)
                .getForecast()
                .getDate();
        Date now = new Date();

        AlertDialog dialog = new AlertDialog
                .Builder(infoFragment.context)
                //.setTitle("Choose an option")
                .create();

        ListView listView = new ListView(getActivity());
        dialog.setView(listView);//use custom ListView
        listView.setAdapter(new UpdateSelectionAdapter(
                infoFragment.context,
                options,
                firstTime.after(now) ? -1 : 1) // disable the second option if it is not possible
        );

        listView.setOnItemClickListener((parent, view, position, id) ->
        {
            // Whenever we click an item, we close the AlertDialog
            DialogUpdateSelection.this.dismiss();
            switch (position)
            {
                case 0:
                    infoFragment.updateRoute(null);
                    break;
                case 1:
                    infoFragment.updateRoute(null);
                    break;
                case 2:
                    infoFragment.showDatePickerAndUpdate();
                    break;
            }
        });

        return dialog;
    }
}
