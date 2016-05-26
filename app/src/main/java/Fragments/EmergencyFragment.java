package Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import edu.uprm.Sentinel.R;

/**
 * This fragment manages the phone numbers table.
 */
public class EmergencyFragment extends Fragment {
    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_emergency ,container, false);

        //get the whole phone table
        TableLayout phonesTable = (TableLayout) rootView.findViewById(R.id.phonestable);
        int count = phonesTable.getChildCount();

        //iterate over all the subtables
        for(int i = 0 ; i < count; i++){
            TableLayout subTable = (TableLayout) phonesTable.getChildAt(i);

            int subcount = subTable.getChildCount();

            //iterate over all the rows with phone numbers
            for(int j = 1; j < subcount ; j++){
                View rowView = subTable.getChildAt(j);

                if(rowView instanceof TableRow){
                    //set the row's listeners
                    rowView.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View arg){
                            TableRow row = (TableRow) arg;

                            TextView v2 = (TextView) row.getChildAt(0);

                            String t = (String) v2.getText();

                            t = (t.replaceAll("\\D", "")); //format string

                            //perform intent on the phone for the formatted phone string
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + t));
                            startActivity(callIntent);
                        }

                    });
                }
            }

        }
        return rootView;
    }

}