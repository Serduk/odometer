package com.sserdiuk.odometer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class OdometerService extends Service {
    private final IBinder binder = new OdometerBinder();
    private static Location lastLocation = null;
    private static double distanceInMeters;

    /**
     * При создании связанной службы вы должны определить реализацию Binder самостоятельно.
     * Мы определим класс с именем OdometerBinder в форме внутреннего класса:
     * */
    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    /**
     * Вызывается при связывании активности со службой.
     *
     * Когда активность связывается со службой через соединение,
     * объект соединения вызывает метод onBind(),
     * который возвращает объект OdometerBinder.
     *
     * Когда активность получает OdometerBinder от соединения,
     * она использует метод getOdometer() для получения объекта OdometerService.
     * */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
//        Listener settings:
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }

                /*
                * Прибавить расстояние
                * между текущей и предыдущей позицией к переменной distanceInMeters,
                * после чего присвоить lastLocation текущую позицию.
                * */
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    public double getKilometers() {
        return this.distanceInMeters / 1000;
    }



}
