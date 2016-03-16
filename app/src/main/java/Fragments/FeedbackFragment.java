package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import edu.uprm.Sentinel.R;

public class FeedbackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "hint";
    private static final String ARG_PARAM3 = "footer";

    // TODO: Rename and change types of parameters
    private String title;
    private String hint;
    private String footer;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            title = getArguments().getString(ARG_PARAM1);
            hint = getArguments().getString(ARG_PARAM2);
            footer = getArguments().getString(ARG_PARAM3);

            System.out.println("print title: " + title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_feedback, container, false);

        TextView titleView = (TextView) root.findViewById(R.id.feedbacktitle);
        AutoCompleteTextView hintView = (AutoCompleteTextView) root.findViewById(R.id.myFeedback);
        TextView footerView = (TextView) root.findViewById(R.id.feedbackmessage);

        titleView.setText(title);
        hintView.setHint(hint);
        footerView.setText(footer);
        return root;
    }

}
