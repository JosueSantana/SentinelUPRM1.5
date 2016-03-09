package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;
import edu.uprm.Sentinel.R;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    ReportFragment report;
    IncidentsFragment incident;
    EmergencyFragment emergency;
    SettingsFragment settings;
    SharedPreferences settingsSP;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager());

        viewPager.addOnPageChangeListener(adapter);

        adapter.addFragment(report, "Alert");

        adapter.addFragment(incident, "Incidents");

        adapter.addFragment(emergency, "Emergency");

        adapter.addFragment(settings, "Settings");

        viewPager.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        incident = new IncidentsFragment();
        emergency = new EmergencyFragment();
        settings = new SettingsFragment();
        report = new ReportFragment();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText(R.string.alert);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error_outline_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText(R.string.incident);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_white_24dp  , 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText(R.string.call);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings_phone_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFour.setText(R.string.settings);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings_applications_white_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private int lastPosition = 0;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            System.out.println("PAGE SCROLLING FROM POSITION:: " + String.valueOf(lastPosition));
            if(lastPosition == 3) {
                Thread postSettingsThread = new Thread(postSettings());
                postSettingsThread.start();
            }
            lastPosition = position;
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        public Runnable postSettings(){

           return new Runnable() {
                @Override
                public void run() {
                    System.out.println("IS THIS RUNNING?!?!?!");
                    final CryptographyHandler crypto;

                    settingsSP = ViewPagerFragment.this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);

                    JSONObject registerJSON = new JSONObject();
                    try {
                        crypto = new CryptographyHandler();

                        registerJSON.put("token", getToken());
                        registerJSON.put(ValuesCollection.EMAIL_KEY, settingsSP.getBoolean("mail", false));
                        registerJSON.put(ValuesCollection.SMS_KEY, settingsSP.getBoolean("sms", false));
                        registerJSON.put(ValuesCollection.PUSH_KEY, settingsSP.getBoolean("push", false));
                        registerJSON.put(ValuesCollection.FAMILY_KEY, settingsSP.getBoolean("family", false));

                        Ion.with(getContext())
                                .load(ValuesCollection.SETTINGS_URL)
                                .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String receivedJSON) {
                                        // Successful Request
                                        if (requestIsSuccessful(e)) {

                                            //JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                            //System.out.println(decryptedValue);

                                            //Context context = getContext();
                                            //CharSequence text = "";
                                            // Received Success Message
                                            /*if (receivedSuccessMessage(decryptedValue)) {
                                                text = "Settings Successfully Updated ";
                                            }
                                            // Message Was Not Successful.
                                            else {
                                                text = "There was an Error Updating Your Settings";
                                            }
                                            int duration = Toast.LENGTH_SHORT;
                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();*/
                                        }
                                        // Errors
                                        else {

                                        }
                                    }

                                    // Extract Success Message From Received JSON.
                                    private boolean receivedSuccessMessage(JSONObject decryptedValue) {
                                        String success = null;
                                        try {
                                            success = decryptedValue.getString("success");
                                            return success.equals("1");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }

                                    // Verify if there was an Error in the Request.
                                    private boolean requestIsSuccessful(Exception e) {
                                        return e == null;
                                    }

                                    // Convert received JSON String into a Decrypted JSON.
                                    private JSONObject getDecryptedValue(String receivedJSONString) {
                                        try {
                                            JSONObject receivedJSON = JSONHandler.convertStringToJSON(receivedJSONString);
                                            String encryptedStringValue = JSONHandler.getSentinelMessage(receivedJSON);
                                            String decryptedStringValue = crypto.decryptString(encryptedStringValue);
                                            JSONObject decryptedJSON = JSONHandler.convertStringToJSON(decryptedStringValue);
                                            return decryptedJSON;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (CryptorException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (CryptorException e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        private String getToken() {
            SharedPreferences credentials = ViewPagerFragment.this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
            String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
            return storedToken;
        }
    }
}
