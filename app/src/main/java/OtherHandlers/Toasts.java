package OtherHandlers;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by jeanmendez on 4/12/16.
 */
public class Toasts {

    public static void genericErrorToast(Activity activity) {
        displayToast(activity, "ERROR! Please Try Again Later.");
    }

    public static void connectionErrorToast(Activity activity) {
        displayToast(activity, "ERROR! Please check your internet connection.");
    }

    private static void displayToast(Activity activity, String message){
        Toast.makeText(activity.getApplicationContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

}
