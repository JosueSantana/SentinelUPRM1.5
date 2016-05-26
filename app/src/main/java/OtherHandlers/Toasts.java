package OtherHandlers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jeanmendez on 4/12/16.
 */
public class Toasts {

    public static void genericErrorToast(Context context) {
        displayToast(context, "ERROR! Please Try Again Later.");
    }

    public static void connectionErrorToast(Context context) {
        displayToast(context, "ERROR! Please check your internet connection.");
    }

    private static void displayToast(Context context, String message){
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

}
