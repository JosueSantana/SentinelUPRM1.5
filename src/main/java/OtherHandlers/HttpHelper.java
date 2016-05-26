package OtherHandlers;

import org.json.JSONObject;
import org.json.JSONException;


/**
 * Created by jeanmendez on 4/3/16.
 */
public class HttpHelper{

    // Verify if there was an Error in the Request.
    public static boolean requestIsSuccessful(Exception e) {
        return e == null;
    }

    public static boolean receivedSuccessMessage(JSONObject decryptedValue, String number){
        String success = null;
        try{
            success = decryptedValue.getString("success");
            return success.equals(number);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return false;
    }


}