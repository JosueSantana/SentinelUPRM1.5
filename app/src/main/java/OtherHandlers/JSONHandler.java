package OtherHandlers;

import org.json.JSONException;
import org.json.JSONObject;

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
        return json.get(Constants.SENTINEL_MESSAGE_KEY).toString();
    }

}
