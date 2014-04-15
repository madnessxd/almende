package alm.motiv.AlmendeMotivator;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Kevin on 15/04/2014.
 */
public class MyLocationListener implements LocationListener {

    public Location location;

    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public String getLocation(){
        return location.getLatitude() + ", " + location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        //GPS enabled
        System.out.println("You're gps is enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        //GPS disabled
        System.out.println("You're gps is disabled");

    }
}

