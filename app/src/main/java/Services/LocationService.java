package Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Manages present location data, sporadically updating it by default
 */
public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public double getCurrentLatitude(){
        return 0;
    }

    public double getCurrentLongitude(){
        return 0;
    }

    private void manageAccuracy(){

    }
    private void fetchCurrentLocation(){

    }
}
