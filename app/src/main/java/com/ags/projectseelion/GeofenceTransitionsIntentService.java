package com.ags.projectseelion;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by tmbro on 19-12-2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
public int id;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "intent error");
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Send notification and log the transition details.

            Intent lbcIntent = new Intent("googlegeofence"); //Send to any reciever listening for this

            lbcIntent.putExtra("ID", Integer.valueOf(triggeringGeofences.get(0).getRequestId()));  //Put whatever it is you want the activity to handle

            LocalBroadcastManager.getInstance(this).sendBroadcast(lbcIntent);  //Send the intent

        } else {
            // Log the error.
            Log.e(TAG, "geofence transition error");
        }
    }
}
