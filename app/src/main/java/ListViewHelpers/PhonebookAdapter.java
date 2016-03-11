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
 * Created by a136803 on 3/3/16.
 */
public class PhonebookAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public PhonebookAdapter(JSONArray jsonArray, Activity activity){
        this.dataArray = jsonArray;
        this.activity = activity;

        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PhonebookRow row;
        // set up convert view if it is null
        if(view == null)
        {
            view = inflater.inflate(R.layout.phonebook_row, null);
            row = new PhonebookRow();

            row.contactName = (TextView) view.findViewById(R.id.phonebookName);
            row.contactPhone = (TextView) view.findViewById(R.id.phonebookNumber);

            view.setTag(row);
        }
        else
        {
            row = (PhonebookRow) view.getTag();
        }

        //edit row data
        try{
            JSONObject jsonObject = this.dataArray.getJSONObject(i);

            row.contactName.setText(jsonObject.getString("name"));
            row.contactPhone.setText(jsonObject.getString("phone"));

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return view;
    }


    private class PhonebookRow
    {
        private TextView contactId;
        private TextView contactName;
        private TextView contactPhone;

    }
}
