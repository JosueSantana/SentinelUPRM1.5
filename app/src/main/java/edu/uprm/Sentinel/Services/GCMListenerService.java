package edu.uprm.Sentinel.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import edu.uprm.Sentinel.MainActivity;
import edu.uprm.Sentinel.R;

import org.json.JSONObject;

import OtherHandlers.JSONHandler;

/**
 * Created by jeanmendez on 3/3/16.
 */
public class GCMListenerService extends GcmListenerService {

    /*
    public void onCreate() {
        System.out.println("gcm service started");
        System.out.println("gcm service started");
        System.out.println("gcm service started");
        System.out.println("gcm service started");
        System.out.println("gcm service started");
        System.out.println("gcm service started");
        System.out.println("gcm service started");
    }
    */

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");
        System.out.println("RECEIVED SOMETHINGGGGGGGGG");

        JSONObject receivedJSON = JSONHandler.convertBundleToJSON(data);
        //Log.d(TAG, "From: " + from);
        //Log.d(TAG, "Message: " + message);


        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sentinelbadge)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
