package OtherHandlers;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by jeanmendez on 2/13/16.
 */
public final class JSONHandler {

    private JSONHandler() {}

    /**
     * Convert a received JSON String into a JSON Object.
     * @param json
     * @return Converted JSON Object
     */
    public static JSONObject convertStringToJSON(String json) throws JSONException {
        return new JSONObject(json);
    }

    /*
    public static JSONArray convertStringToJSON(String json) throws JSONException {
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    /**
     * Convert a JSON object into a String.
     * @param json
     * @return JSON Object in String Format
     */
    public static String convertJSONToString(JSONObject json) {
        return json.toString();
    }

    /**
     * Get the Value attached to the "SentinelMessage" Key from the JSON Object.
     * @param json
     * @return Value for "SentinelMessage" key off JSON.
     */
    public static String getSentinelMessage(JSONObject json) throws JSONException {
        return json.get(ValuesCollection.SENTINEL_MESSAGE_KEY).toString();
    }

    public static JSONObject convertBundleToJSON(Bundle data) {
        JSONObject json = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(data.get(key)));
            } catch(JSONException e) {
                //Handle exception here
            }
        }
        return json;
    }
}
