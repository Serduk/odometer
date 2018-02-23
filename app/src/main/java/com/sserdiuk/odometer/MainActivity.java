package com.sserdiuk.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private OdometerService odometerService;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) iBinder;

//            Преобразовать Binder
//            в OdometerBinder, после чего использовать для получения ссылки на OdometerService.
            odometerService = odometerBinder.getOdometer();
//            Если активность связана со службой, bound присваивается true.
            bound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        watchKilometrage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void watchKilometrage() {
        final TextView distanceView = (TextView) findViewById(R.id.distance);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometerService != null) {
                    distance = odometerService.getKilometers();
                }
                String distanceString = String.format("%1$,.2f kilometers", distance);
                distanceView.setText(distanceString);
                /*
                * Передать код в объекте Runnable
                * для повторного выполнения с задержкой 1000 миллисекунд (то есть 1 секунда).
                * Так как эта строка кода включена в метод run() объекта Runnable,
                * она будет выполняться каждую секунду (возможно, с небольшой задержкой).
                * */
                handler.postDelayed(this, 1000);
            }
        });
    }
}
