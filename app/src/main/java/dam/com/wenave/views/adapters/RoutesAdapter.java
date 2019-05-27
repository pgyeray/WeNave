package dam.com.wenave.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dam.com.wenave.R;
import dam.com.wenave.models.Route;
import dam.com.wenave.persistence.dataaccesslayer.DataAccessLayer;
import dam.com.wenave.views.activities.MainActivity;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>
{
    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView option;
        TextView summary;
        TextView creationDate;
        TextView initDate;
        TextView endDate;
        FloatingActionButton erase;
        ImageView bg;

        RouteViewHolder(View itemView)
        {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.info_route_option);
            summary = (TextView) itemView.findViewById(R.id.summary_routes);
            creationDate = (TextView) itemView.findViewById(R.id.creationDate);
            initDate = (TextView) itemView.findViewById(R.id.initDate);
            endDate = (TextView) itemView.findViewById(R.id.endDate);
            erase = (FloatingActionButton) itemView.findViewById(R.id.deleteRoute);
            bg = (ImageView) itemView.findViewById(R.id.background_layout);
            erase.setOnClickListener(v -> delete(getAdapterPosition()));

            Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down_top);
            itemView.setAnimation(a);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            context.launchExistingRoute(
                    (Route) routes.get(getAdapterPosition())
            );
        }

        /**
         * Remove the Route at position pos
         * @param pos position where the Route is at
         */
        void delete(int pos)
        {
            DataAccessLayer.setUpDb(null);
            DataAccessLayer.deleteRoute(routes.get(pos));
            routes.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    private MainActivity context;
    private List<Route> routes;

    public RoutesAdapter(List<Route> routes, MainActivity context)
    {
        this.routes = routes;
        this.context = context;
    }

    @Override
    public int getItemCount()
    {
        return routes.size();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.saved_routes_cardview,
                        viewGroup,
                        false);
        return new RouteViewHolder(v);
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position)
    {
        Route r = (Route) routes.get(position);

        holder.option.setText(r.getOption());
        holder.summary.setText(r.routeTitle);
        holder.creationDate.setText(String.format(
                Locale.getDefault(),
                "%s: %s",
                context.getString(R.string.cardview_creation_date),
                dateFormat(r.getCreationDate()) )
        );
        holder.initDate.setText(String.format(
                Locale.getDefault(),
                "%s: %s",
                context.getString(R.string.cardview_start),
                dateFormat(r.getRoutePoints()
                                .get(0)
                                .getForecast()
                                .getDate())
                )
        );
        holder.endDate.setText(String.format(
                Locale.getDefault(),
                "%s: %s",
                context.getString(R.string.cardview_end),
                dateFormat(r.getRoutePoints()
                                .get(r.getRoutePoints().size() - 1)
                                .getForecast()
                                .getDate())
                )
        );

        int id = getImageId(context);

        try
        {
            holder.bg.setBackgroundResource(id);
            holder.bg.setAlpha(0.2f);
        }
        catch (Exception e)
        {
            Log.e("WeNAVE", String.format("No id {%d} found", id));
            Log.e("WeNAVE", e.getMessage());
        }
    }

    @Override
    public void onAttachedToRecyclerView (@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String dateFormat (Date date)
    {
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df =  new SimpleDateFormat(pattern, Locale.getDefault());
        return df.format(date);
    }

    private int getImageId (Context context)
    {
        int res = (int) (Math.random() * 3) + 1;
        String imageName = "bg" + res;
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
