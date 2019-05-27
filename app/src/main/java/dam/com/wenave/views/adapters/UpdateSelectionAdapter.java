package dam.com.wenave.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dam.com.wenave.R;

public class UpdateSelectionAdapter extends BaseAdapter
{
    private String[] data;
    //private LayoutInflater layoutInflater;
    private int selectPosition = -1;//disabled item position
    private Context ctx;

    public void select(int selectPosition)
    {
        this.selectPosition = selectPosition;
    }

    public int getSelectPosition()
    {
        return selectPosition;
    }

    public UpdateSelectionAdapter(Context contexts, String[] data, int selectPosition)
    {
        //layoutInflater = LayoutInflater.from(contexts);
        this.ctx = contexts;
        this.data = data;
        this.selectPosition = selectPosition;
    }

    @Override
    public int getCount()
    {
        return data.length;
    }

    @Override
    public Object getItem(int i)
    {
        return data[i];
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {

        if (view == null) {
            view = LayoutInflater.from(ctx).
                    inflate(R.layout.selection_on_update, viewGroup, false);
        }

        TextView selection = view.findViewById(R.id.item_selection);//(TextView) layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);//use system default layout

        //textView.setPadding(20, 20, 20, 20);
        selection.setText(data[i]);//set data

        selection.setEnabled(this.selectPosition != i);//disable or enable
        return view;
    }
}