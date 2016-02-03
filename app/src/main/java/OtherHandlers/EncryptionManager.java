package OtherHandlers;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.JNCryptor;

/**
 * Created by jeanmendez on 2/3/16.
 */

/*
    This class handles the Encryption and Decryption of all data sent and received
    to and from the server.
 */

public class EncryptionManager {
    private JNCryptor cryptor;
    private final String SENTINEL_ENCRYPTION_KEY = "62rCTnekkNI1rHTa3ygTO9dOwUJTuZUf";

    public EncryptionManager() {
        this.cryptor = new AES256JNCryptor();
    }

    public String encryptJSON(String json) {
        return null;
    }

    public String decryptJSON(String json) {
        return null;
    }

}
