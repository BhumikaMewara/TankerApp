package com.kookyapps.gpstankertracking.Services;

import android.location.Location;

public interface TankerLocationCallback {
    void abortListener(int abortedBy);
    void newLocation(Location location);
    void geofenceEnter();
    void geofenceExit();
}
