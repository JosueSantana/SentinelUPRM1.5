package OtherHandlers;
import android.util.Base64;
import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
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
    final String SENTINEL_ENCRYPTION_KEY = "62rCTnekkNI1rHTa3ygTO9dOwUJTuZUf";

    // Standard Constructor
    public CryptographyHandler() throws JSONException {
        this.cryptor = new AES256JNCryptor();
    }

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

    public String decryptJSON(String json) throws CryptorException {
        byte[] decryptedMessageArray = this.cryptor.decryptData(Base64.decode(json, 0), SENTINEL_ENCRYPTION_KEY.toCharArray());
        return new String(decryptedMessageArray, StandardCharsets.UTF_8);
    }
}