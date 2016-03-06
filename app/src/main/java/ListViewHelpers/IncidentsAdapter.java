package ListViewHelpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.uprm.Sentinel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a136803 on 12/18/15.
 */
public class IncidentsAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public IncidentsAdapter(JSONArray jsonArray, Activity activity) {
        this.dataArray = jsonArray;
        this.activity = activity;

        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.dataArray.length();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View view, ViewGroup parent) {

        IncidentsRow row;
        // set up convert view if it is null
        if(view == null)
        {
            view = inflater.inflate(R.layout.incidents_row, null);
            row = new IncidentsRow();

            row.locationName = (TextView) view.findViewById(R.id.incidentRowRegion);
            row.incidentTime = (TextView) view.findViewById(R.id.incidentRowTime);
            row.incidentDate = (TextView) view.findViewById(R.id.incidentRowDate);

            view.setTag(row);
        }
        else
        {
            row = (IncidentsRow) view.getTag();
        }

        //edit row data
        try{
            JSONObject jsonObject = this.dataArray.getJSONObject(i);

            row.locationName.setText(jsonObject.getString("name"));
            row.incidentTime.setText(jsonObject.getString("time"));
            row.incidentDate.setText(jsonObject.getString("date"));

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return view;
    }


    private class IncidentsRow
    {
        private TextView locationName;
        private TextView incidentTime;
        private TextView incidentDate;

    }
}
