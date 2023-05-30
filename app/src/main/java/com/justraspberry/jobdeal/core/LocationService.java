package com.justraspberry.jobdeal.core;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.event.LocationEvent;
import com.justraspberry.jobdeal.model.CurrentLocation;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;

import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class LocationService extends Service {
    public static boolean isRunning = false;
    public static boolean fineLocation = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationCallback locationCallback;
    private LocationSettingsRequest locationSettingsRequest;
    LocationRequest locationRequest;

    public LocationService() {
        super();
        //EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startLocationService(Context context, boolean fineLocation) {
        try {
            Intent intent = new Intent(context, LocationService.class);
            LocationService.fineLocation = fineLocation;
            context.startService(intent);

            Timber.e("Location Service started...");
        } catch (Exception e){
            Timber.e(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopLocationService(Context context) {
        try {
            Intent intent = new Intent(context, LocationService.class);
            LocationService.fineLocation = false;
            intent.setAction("close");
            context.startService(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("close")) {
                    isRunning = false;
                    if(locationCallback!=null)
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    Timber.e("Stop Service");
                    stopSelf();
                    return START_NOT_STICKY;
                }
            }
        }
        isRunning = true;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        Timber.e("Location Service running...");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Timber.e("LOCATION: " + locationResult.getLastLocation().toString());

                App.getInstance().setLocation(locationResult.getLastLocation());
                EventBus.getDefault().postSticky(new LocationEvent(locationResult.getLastLocation()));

                ApiRestClient.getInstance().updateLocation(new CurrentLocation(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()));

                if(locationResult.getLastLocation().hasAccuracy() && locationResult.getLastLocation().getAccuracy() < 60
                && System.currentTimeMillis() - locationResult.getLastLocation().getTime() < 60 * 60 * 1000)
                    LocationService.stopLocationService(getApplicationContext());
            }
        };

        startLocationUpdates();

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    private void startLocationUpdates() {
        locationRequest = getLocationRequest();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        //location setting helper
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();


        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Timber.e("LOCATION SETTING NOT OK!");
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Timber.e("LOCATION RESOLUTION_REQUIRED!");
                        //EventBus.getDefault().post(new LocationSettingsEvent((ApiException) e));
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                }
            }
        });
    }

    private LocationRequest getLocationRequest() {
        final LocationRequest locationRequest = new LocationRequest();
        if (!fineLocation) {
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        return locationRequest;
    }
}
