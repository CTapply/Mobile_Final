package geofence;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

/**
 * Helper functions for interacting with Geofences
 * Created by tjvalcourt on 2/27/2017.
 */

public class GeofenceAssets {

    private static final int LOITERING_DELAY = 5 * 60 * 1000; // minutes * seconds * millis

    /**
     * Builds a geofence object with the given parameters
     * @param id ID to identify this geofence, based on commute name
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Geofence object with these paremeters
     */
    public static void addGeofence(String id, double latitude, double longitude) {
        GeofenceTransitionIntentService.geofences.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(id)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        150 // meters
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(NEVER_EXPIRE)

                // How long the person has to be within the geofence for it to consider it a transition
                .setLoiteringDelay(LOITERING_DELAY)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)

                // Create the geofence.
                .build());
    }

    /**
     * Checks if the geofence exists in the list or not already
     * @param id ID of the geofence based on the commute
     * @return TRUE if it's in the list already, FALSE otherwise
     */
    public static boolean geofenceExists(String id){
        for(Geofence g : GeofenceTransitionIntentService.geofences){
            if(g.getRequestId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
