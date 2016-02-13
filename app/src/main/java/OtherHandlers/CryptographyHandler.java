package OtherHandlers;

import android.util.Base64;
import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
import org.cryptonode.jncryptor.InvalidHMACException;
import org.cryptonode.jncryptor.JNCryptor;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by jeanmendez on 2/3/16.
 */

/*
    This class handles the Encryption and Decryption of all data sent and received
    to and from the server.
 */

public final class CryptographyHandler {
    
    private JNCryptor cryptor;

    private String email;
    private String phone;
    private String os;
    private String deviceID;

    private JSONObject json;
    final String SENTINEL_ENCRYPTION_KEY = "62rCTnekkNI1rHTa3ygTO9dOwUJTuZUf";

    // Standard Constructor
    public CryptographyHandler() throws JSONException {
        this.cryptor = new AES256JNCryptor();
    }

    // Test Constructor
    /*
    public CryptographyHandler() throws JSONException {
        this.cryptor = new AES256JNCryptor();

        this.email = "jean.mendez2@upr.edu";
        this.phone = "7875293812";
        this.os = "android";
        this.deviceID = "unavailable";

        jsonSetup();
    }
    */


    public String encryptJSON(JSONObject json) throws CryptorException {
        // Encrypt JSON String into Byte Array.
        byte[] encryptedByteArray = this.cryptor.encryptData(json.toString().getBytes(), this.SENTINEL_ENCRYPTION_KEY.toCharArray());

        // Encode encrypted byte array to Base64.
        return Base64.encodeToString(encryptedByteArray, Base64.DEFAULT);
    }

    public String decryptString(String encryptedString) throws CryptorException {
        // Decrypt the String into a Byte Array.
        byte[] decryptedMessageArray = this.cryptor.decryptData(Base64.decode(encryptedString, 0), this.SENTINEL_ENCRYPTION_KEY.toCharArray());

        // Convert Byte Array into a UTF-8 String and return String.
        return new String(decryptedMessageArray, StandardCharsets.UTF_8);
    }

    /*
    public JSONObject getJSONToSent() throws CryptorException {

        JSONObject temp = new JSONObject();

        //String encryptedJson = this.encryptJSON();

        System.out.println("Encrypted JSON Is: ");
        try {
            temp.accumulate("SentinelMessage", encryptedJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
    */

    public String decryptJSON(String json) throws CryptorException {
        byte[] decryptedMessageArray = this.cryptor.decryptData(Base64.decode(json, 0), SENTINEL_ENCRYPTION_KEY.toCharArray());
        return new String(decryptedMessageArray, StandardCharsets.UTF_8);
    }

    public String getJsonString(){
        return this.json.toString();
    }


    private void jsonSetup() throws JSONException {
        this.json = new JSONObject();

        this.json.accumulate("email", this.email);
        this.json.accumulate("phone", this.phone);
        this.json.accumulate("os", this.os);
        this.json.accumulate("deviceID", this.deviceID);
    }

}

/*
            JSONObject receivedJSON = new JSONObject();
            receivedJSON.put("SentinelMessage", "AwFXWwsGAXxKtBU+tZsX9d0qGMjxGUY9zhi+Rizvwhj61wDH2M36LoJe31yCFsw/0IoqaXXpfOa/2SOIJoZ3CrYpv4b53dNQZQxbi5QLMg9AKA==");

            String receivedJSONString = receivedJSON.toString();

            System.out.println(receivedJSONString);
            System.out.println("lol");

            JSONObject convertedJSON = new JSONObject(receivedJSONString);
            String receivedKey = convertedJSON.get("SentinelMessage").toString();

            System.out.println(receivedKey);
            JNCryptor encryptor = new AES256JNCryptor();
            byte[] decryptedMessageArray = encryptor.decryptData(Base64.decode(receivedKey, 0), SENTINEL_ENCRYPTION_KEY.toCharArray());

            String decryptedMessage = new String(decryptedMessageArray, StandardCharsets.UTF_8);
            System.out.println(decryptedMessage);
 */