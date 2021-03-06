package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import edu.uprm.Sentinel.R;

public class PoliciesFragment extends Fragment {

    TextView text;

    public PoliciesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_policies, container, false);

        WebView wv = (WebView) rootView.findViewById(R.id.termsPage);

        wv.setWebViewClient(new SwAWebClient());
        wv.getSettings().setJavaScriptEnabled(true);

        String pdf = "http://www.uprm.edu/politicas//politicas01.pdf";
        wv.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
        return rootView;
    }


    private class SwAWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

}
