package OtherHandlers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * This service activates when sending an alert and disables alerts until the Timer complies with
 * the established time.
 */
public class UsageTimerService extends Service {
    public UsageTimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void onStartCommand(){}

    private void runTimer(){}

    private void onTimeOut(){}
}
