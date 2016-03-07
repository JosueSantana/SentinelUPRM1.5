package OtherHandlers;

import android.provider.Settings;

/**
 * Created by a136803 on 1/8/16.
 */
public class ValuesCollection {

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

    /*
    public static final String SEND_INCIDENT_URL = INITIAL_URL + "incident/31923faa-340c-4707-8c93-df528e65aa41/incident-request/7c0d78d4-4b78-4ee2-9e8e-4b4d6988a855";

    public static final String FETCH_INCIDENT_URL = INITIAL_URL + "incident/588527d9-3c44-424b-9407-9e84729cde26/incidentlog/08b18427-3722-4ac3-ac5c-2e7c99bbea05";
    public static final String SEND_LOGIN_DATA_URL= INITIAL_URL + "login/1054cb75-37c8-4225-ba3d-5a3d7b7ca13f/userlog/69611cd4-494a-4934-a4be-ddbf6bb5550c";
    public static final String LOG_OUT_URL = INITIAL_URL + "login/c0d9e754-70d4-4278-859c-9e80a39461af/userlogout/368d23df-05b3-454e-b58c-9ce139eb00d6";

    public static final String TOKEN_VALUE = "SENTINEL_UPRM_TOKEN_VALUE";
    public static final String EMAIL_VALUE = "SENTINEL_UPRM_EMAIL_VALUE";
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

    public static final long TIMER_PERIOD = 2;

    public static final String ANDROID_SENDER_ID = "6008874123";
}

/*
    static let feedback = ["route": "/1ea13556cc62590a90/report/8679df6f1ae36d9395/feedback", "type": "PUT"]

    static let problem = ["route": "/8679df6f1ae36d9395/report/c525b9ff1ae36d9395/problem", "type": "PUT"]

    static let userSession = ["route": "/de41089f1ae36d9395/user/ec8d28cf1ae36d9395/session", "type": "POST"]

    static let userValidation = ["route": "/d687a815a1cb71f947/user/ea641d11e4e7bffd/validate", "type": "POST"]

    static let contacts = ["route": "/1123c1a441f7dfef5eb/contact/408d27f6ce0a895c8f/contacts", "type": "POST"]

    static let contactAdd = ["route": "/f3df283311e4e7bffd/contact/194d10f311e4e7bffd/add", "type": "PUT"]

    static let contactEdit = ["route": "/47abedc1a15c9f73/contact/98aee6f6fbbbf06133/edit", "type": "POST"]

    static let contactDelete = ["route": "/580b3b11e4e7bffd/contact/133757f42b99c7903/delete", "type": "DELETE"]
 */
