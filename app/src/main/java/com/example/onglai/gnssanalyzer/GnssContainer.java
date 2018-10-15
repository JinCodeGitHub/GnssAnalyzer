package com.example.onglai.gnssanalyzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GnssContainer {

    public static final String TAG = "GnssContainer";

    private final List<GnssListener> mLoggers;

    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(5L);

    private final LocationManager mLocationManager;
    private final LocationListener mLocationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    for (GnssListener logger: mLoggers) {
                        logger.onLocationChanged(location);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //TODO: Implement LocationListener.onStatusChanged

                }

                @Override
                public void onProviderEnabled(String provider) {
                    //TODO: Implement LocationListener.onProviderEnabled

                }

                @Override
                public void onProviderDisabled(String provider) {
                    //TODO: Implement LocationListener.onProviderDisabled

                }
            };

    private final GnssMeasurementsEvent.Callback gnssMeasurementEventCallback =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent events) {
                    for (GnssListener logger: mLoggers) {
                        logger.onGnssMeasurementsReceived(events);

                    }
                }
            };

    private final GnssStatus.Callback gnssStatusCallback =
            new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                }

                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    for (GnssListener logger: mLoggers) {
                        logger.onSatelliteStatusChanged(status);
                    }
                }
            };

    public GnssContainer(Context context, GnssListener... loggers) {
        this.mLoggers = Arrays.asList(loggers);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public LocationManager getLocationManager() {return  mLocationManager; }

    @SuppressLint("MissingPermission")
    public void registerLocation() {
        boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGpsProviderEnabled) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_RATE_NETWORK_MS,
                    0.0f,
                    mLocationListener);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_RATE_GPS_MS,
                    0.0f,
                    mLocationListener);
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerGnssMeasurements() {
        mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementEventCallback);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unregisterGnssMeasurements() {
        mLocationManager.unregisterGnssMeasurementsCallback(gnssMeasurementEventCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    public void registerGnssStatusChanged() {
        mLocationManager.registerGnssStatusCallback(gnssStatusCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unRegisterGnssStatusCallback() {
        mLocationManager.unregisterGnssStatusCallback(gnssStatusCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unregisterAll() {
        if(mLocationManager != null) {
            if(gnssMeasurementEventCallback != null) {
                mLocationManager.unregisterGnssMeasurementsCallback(gnssMeasurementEventCallback);
            }
            if(gnssStatusCallback != null) {
                mLocationManager.unregisterGnssStatusCallback(gnssStatusCallback);
            }
            if(mLocationListener != null) {
                mLocationManager.removeUpdates(mLocationListener);
            }
        }
    }

}
