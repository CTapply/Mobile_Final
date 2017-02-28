package geofence;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.example.jeffrey.finalprototype.CommuteListActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tjvalcourt on 2/27/2017.
 */

public class GeofenceTransitionIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GeofenceTransitionIntentService() {
        super("geofence");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Error checking ???

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            // There should only ever be on triggering geofence
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(triggeringGeofences);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
        } else {
            // Log the error.
        }
    }

    /**
     * Notification sends an update message for each transition that gets updated
     * This is the current time in hours and minutes when the geofence was triggered
     * @param transitionDetails in format transitionString:geofenceID
     */
    private void sendNotification(String transitionDetails){
        Intent intent = new Intent(getApplicationContext(), CommuteListActivity.class); // create an intent to send to commute list

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);

        intent.putExtra("hour", hour);
        intent.putExtra("minute", minutes);
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = "dwell";

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }
}
