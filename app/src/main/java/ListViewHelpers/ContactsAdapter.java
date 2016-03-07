package ListViewHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import edu.uprm.Sentinel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a136803 on 12/18/15.
 * This class holds the data structure for a contacts list item.
 */
public class ContactsAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public ContactsAdapter(JSONArray jsonArray, Activity activity){
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

        ContactsRow row;
        // set up convert view if it is null
        if(view == null)
        {
            view = inflater.inflate(R.layout.contacts_row, null);
            row = new ContactsRow();

            row.contactName = (TextView) view.findViewById(R.id.myContactName);
            row.contactPhone = (TextView) view.findViewById(R.id.myContactPhone);

            ((RelativeLayout) view ).getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                                                          public void onClick(View arg) {
                                                                              //do stuff
                                                                              System.out.println("DOING DELETING STUFF!!!");
                                                                          }
                                                                      });

            view.setTag(row);
        }
        else
        {
            row = (ContactsRow) view.getTag();
        }

        //edit row data
        try{
            JSONObject jsonObject = this.dataArray.getJSONObject(i);

            row.contactName.setText(jsonObject.getString("name"));
            row.contactPhone.setText(jsonObject.getString("editedPhone"));

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return view;
    }


    private class ContactsRow
    {
        private TextView contactName;
        private TextView contactPhone;

    }
}
