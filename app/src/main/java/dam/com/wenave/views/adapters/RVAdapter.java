package dam.com.wenave.views.adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dam.com.wenave.R;
import dam.com.wenave.managements.CameraManagement;
import dam.com.wenave.managements.MapBoxManagement;
import dam.com.wenave.models.CustomPoint;
import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.views.activities.MainActivity;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ForecastViewHolder>
{
    public class ForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView iv;
        TextView title;
        TextView summary;
        TextView precipitation;
        TextView humidity;
        TextView date;
        ImageView cardView;

        CustomPoint point;

        ForecastViewHolder(View itemView)
        {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.weather_img);
            title = (TextView) itemView.findViewById(R.id.temperature);
            summary = itemView.findViewById(R.id.summary);
            precipitation = (TextView) itemView.findViewById(R.id.precipitation);
            humidity = (TextView) itemView.findViewById(R.id.humidity);
            date = (TextView) itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.card_layout);

            itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down_top));

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            // TODO https://docs.mapbox.com/android/maps/examples/marker-symbol-layer/
            management.mapbox.getStyle(style ->
            {
                final SymbolLayer selectedSymbolLayer = (SymbolLayer) style.getLayer(point.layerId);
                selectSymbolLayer(selectedSymbolLayer, point);

                superSelectedSymbolLayer = selectedSymbolLayer;
            });

            if (selectedView != null)
            {
                selectedView.setBackgroundColor(context.getResources().getColor(R.color.deselected_cardview));
            }

            v.setBackgroundColor(context.getResources().getColor(R.color.selected_cardview));
            selectedView = v;
        }
    }

    List<RoutePoint> routePoints;
    Activity context;
    MapBoxManagement management;

    SymbolLayer superSelectedSymbolLayer = null;
    View selectedView;

    public RVAdapter(List<RoutePoint> routePoints, MainActivity context, MapBoxManagement management)
    {
        this.routePoints = routePoints;
        this.context = context;
        this.management = management;
    }

    @Override
    public int getItemCount()
    {
        return routePoints.size();
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.forecast_cardview, viewGroup, false);
        //return new ForecastViewHolder(v);
        return new ForecastViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position)
    {
        Double hum = routePoints.get(position).getForecast().getHumidity();
        String temp = routePoints.get(position).getForecast().getTemperature().toString();
        temp = temp.substring(0,2);

        String summary = routePoints.get(position).getForecast().getSummary();

        holder.title.setText(temp+"º");
        holder.summary.setText(summary);
        holder.precipitation.setText(context.getString(R.string.cardview_rain) + ": " + routePoints.get(position).getForecast().getPrecipitation().toString() + " mm");
        holder.humidity.setText(context.getString(R.string.cardview_humidity) + ": " + String.format("%.2f", hum * 100) + "%");
        holder.date.setText(dateFormat(routePoints.get(position).getForecast().getDate()));
        int id = getImageId(context, routePoints.get(position).getForecast().getIcon());
        holder.iv.setImageResource(id);
        id = getBGId(context, routePoints.get(position).getForecast().getIcon());

        holder.cardView.setBackgroundResource(id);
        holder.cardView.setAlpha(0.3f);

        Animation normal = AnimationUtils.loadAnimation(context, R.anim.scroll_animation_normal);
        Animation large = AnimationUtils.loadAnimation(context, R.anim.scroll_animation_large);
        if(summary.length() > 25) holder.summary.startAnimation(large);
        else holder.summary.startAnimation(normal);


        holder.point = routePoints.get(position).getCustomPoint();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private int getImageId(Context context, String imageName)
    {
        imageName = imageName.replace("-","_");
        return context.getResources()
                .getIdentifier("drawable/" + imageName,
                        null,
                        context.getPackageName());
    }
    public int getBGId(Context context, String imageName) {
        imageName = imageName.replace("-","_");
        return context.getResources().getIdentifier("drawable/" + imageName+"_b", null, context.getPackageName());
    }

    private String dateFormat(Date date)
    {
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    private void selectSymbolLayer(SymbolLayer symbolLayer, CustomPoint point)
    {
        if (superSelectedSymbolLayer != null)
        {
            deselectSymbolLayer(superSelectedSymbolLayer);
        }

        CameraManagement.animateCamera(
                management.mapbox,
                point.castToPoint(),
                1000);

        ValueAnimator markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(1f, 2f);
        markerAnimator.setDuration(300);
        markerAnimator.addUpdateListener(animator ->
        {
            symbolLayer.setProperties(
                    PropertyFactory.iconSize((float) animator.getAnimatedValue())
            );
        });
        markerAnimator.start();
    }

    private void deselectSymbolLayer (SymbolLayer symbolLayer)
    {
        ValueAnimator markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(2f, 1f);
        markerAnimator.setDuration(300);
        markerAnimator.addUpdateListener(animator ->
        {
            symbolLayer.setProperties(
                    PropertyFactory.iconSize((float) animator.getAnimatedValue())
            );
        });
        markerAnimator.start();
    }

    private class AnimatedTextView extends androidx.appcompat.widget.AppCompatTextView
    {

        public AnimatedTextView(Context context)
        {
            super(context);
        }

        @Override
        protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            if (focused)
                super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            if (hasWindowFocus)
            {
                super.onWindowFocusChanged(hasWindowFocus);
            }
        }

        @Override
        public boolean isFocused() {
            return true;
        }
    }
}
