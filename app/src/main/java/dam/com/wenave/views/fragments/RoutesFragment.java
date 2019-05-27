package dam.com.wenave.views.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dam.com.wenave.R;
import dam.com.wenave.models.Route;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.views.activities.MainActivity;
import dam.com.wenave.views.adapters.RoutesAdapter;

public class RoutesFragment extends Fragment
{
    private TextView title;
    private FloatingActionButton eraseAll;

    private List<Route> routes;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootview = inflater.inflate(R.layout.saved_routes, container, false);
        MainActivity context = (MainActivity) getActivity();

        routes = (List<Route>) DataAccessLayer.getRoutes();

        title = rootview.findViewById(R.id.current_routes);
        eraseAll = (FloatingActionButton) rootview.findViewById(R.id.removeAll);
        RecyclerView rv = rootview.findViewById(R.id.saved_routes);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        assert context != null;
        title.setText(context.getString(R.string.nav_my_routes));

        eraseAll.setOnClickListener(v ->
        {
            DataAccessLayer.deleteRoutes();
            routes.clear();

            // Reset the adapter
            rv.setAdapter(new RoutesAdapter(new ArrayList<>(routes), context));
        });

        // Test of recyclerview
        RoutesAdapter adapter = new RoutesAdapter(routes, context);
        rv.setAdapter(adapter);
        return rootview;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
