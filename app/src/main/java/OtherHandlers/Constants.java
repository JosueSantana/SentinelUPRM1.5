package OtherHandlers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by a136803 on 1/8/16.
 */
public class Constants {

    /**
     * Routes:
     */
    //public static final String INITIAL_URL = "https://136.145.219.61:7213/";
    public static final String INITIAL_URL = "https://sentinel.uprm.edu:7213/";

    // Registration and Sign In.
    public static final String REGISTER_URL = INITIAL_URL + "de41089f1ae36d9395/user/ec8d28cf1ae36d9395/session";
    public static final String PASSCODE_VALIDATION_URL = INITIAL_URL + "d687a815a1cb71f947/user/ea641d11e4e7bffd/validate";

    // Alerts
    public static final String SEND_ALERT_URL = INITIAL_URL + "31923fa28e65aa41/alert/7c0d78d4b4d6988a855/request";
    public static final String GET_ALERTS_URL = INITIAL_URL + "588527de84729cde26/incident/08b1842e7c99bbea05/logs";

    // Settings
    public static final String SETTINGS_URL = INITIAL_URL + "8dc8580836be036a/user/c8d7b2a2309baa3746/preferences";

    // Contacts
    public static final String CONTACT_LIST_URL = INITIAL_URL + "1123c1a441f7dfef5eb/contact/408d27f6ce0a895c8f/contacts";
    public static final String ADD_CONTACT_URL = INITIAL_URL + "f3df283311e4e7bffd/contact/194d10f311e4e7bffd/add";
    public static final String DELETE_CONTACT_URL = INITIAL_URL + "580b3b11e4e7bffd/contact/133757f42b99c7903/delete";

    // Report Problem and Send Feedback
    public static final String REPORT_PROBLEM_URL = INITIAL_URL + "8679df6f1ae36d9395/report/c525b9ff1ae36d9395/problem";
    public static final String SEND_FEEDBACK_URL = INITIAL_URL + "1ea13556cc62590a90/report/8679df6f1ae36d9395/feedback";

    // Unsubscribe
    public static final String UNSUBSCRIBE_URL = INITIAL_URL + "db3aa951749409372/user/ee50ada0acefb7ac89b/unsubscribe";

    /**
     * Other Constants:
     */
    public static final String SETTINGS_SP = "SettingsFile";
    public static final String CREDENTIALS_SP = "CredentialsFile";
    public static final String TOKEN_KEY = "token";

    // Settings Values:
    public static final String EMAIL_KEY = "mail";
    public static final String PUSH_KEY = "push";
    public static final String FAMILY_KEY = "family";
    public static final String SMS_KEY = "sms";

    public static final String INSTANCE_ID_GCM = "sentinel_instance_id";
    public static final String SENTINEL_MESSAGE_KEY = "SentinelMessage";
    public static final String ANDROID_OS_STRING = "android";
    public static final String ANDROID_SENDER_ID = "682306700573";
    public static final long TIMER_PERIOD = 0;

    /**
     * Get Token
     */
    public static String getToken(Context context) {
        SharedPreferences credentials = context.getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(Constants.TOKEN_KEY, null);
        return storedToken;
    }

    /**
     * Store Email
     */
    public static void storeEmail(Context context, String email) {
        storeValue(context, email, EMAIL_KEY);
    }
    /**
     * Store Token
     */
    public static void storeToken(Context context, String token) {
        storeValue(context, token, TOKEN_KEY);
    }

    private static void storeValue(Context context, String value, String key){
        SharedPreferences credentials = context.getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        SharedPreferences.Editor credentialsEditor = credentials.edit();
        credentialsEditor.putString(key, value);
        credentialsEditor.commit();
    }
}
