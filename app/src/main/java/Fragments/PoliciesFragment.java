package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

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
        wv.loadUrl("http://sentinel.ece.uprm.edu/terms");

        return rootView;
    }


    private class SwAWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

}
