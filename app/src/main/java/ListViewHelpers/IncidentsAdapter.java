package ListViewHelpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import java.util.List;

/**
 * Created by a136803 on 12/18/15.
 */
public class IncidentsAdapter extends ArrayAdapter {

    private Context context;

public IncidentsAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View viewToUse;
        IncidentsListItem item = (IncidentsListItem)getItem(position);


        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            viewToUse = mInflater.inflate(R.layout.incidents_row, null);
            holder = new ViewHolder();
            holder.time = (TextView)viewToUse.findViewById(R.id.time);
            holder.date = (TextView)viewToUse.findViewById(R.id.date);
            holder.region = (TextView)viewToUse.findViewById(R.id.region);
            viewToUse.setTag(holder);

        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.time.setText(item.getCreatedOn().substring(0, 10));
        holder.date.setText(item.getCreatedOn().substring(11, item.getCreatedOn().length()-1));
        holder.region.setText(item.getIncidentRegion());
        this.notifyDataSetChanged();
        return viewToUse;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView time;
        TextView date;
        TextView region;
    }

    private void getItem(){

    }

    private void getItemId(){

    }

    /*private void getCount(){

    }*/

}
