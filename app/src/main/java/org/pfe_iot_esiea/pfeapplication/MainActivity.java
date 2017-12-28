package org.pfe_iot_esiea.pfeapplication;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.EddystoneDevice;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ProximityManager proximityManager;
    private boolean Beacon1recognized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KontaktSDK.initialize("NOpUjpWwpCckCePHQnjZngXENPFcmZMj");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button monbutton = (Button) findViewById(R.id.button_scan);
        monbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proximityManager.restartScanning();
            }
        });

        if(mBluetoothAdapter == null){

        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                mBluetoothAdapter.enable();
            }
        }
        proximityManager = ProximityManagerFactory.create(this);
        configureProximityManager();
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());


    }

    private void configureProximityManager() {
        proximityManager.configuration()
                .scanMode(ScanMode.LOW_LATENCY)
                .scanPeriod(ScanPeriod.create(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(20)))
                .activityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                .forceScanConfiguration(ForceScanConfiguration.MINIMAL);

    }



    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }

        });
    }



    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i("Sample", "IBeacon discovered: " + ibeacon.toString());

            }
        };
    }



    private EddystoneListener createEddystoneListener() {



        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.i("Sample", "Eddystone discovered: " + eddystone.toString() +  " Name : " + eddystone.getName() );

                TextView monTexte = (TextView)findViewById(R.id.beacon1);
                if(!Beacon1recognized) {
                    if(eddystone.getInstanceId().equals("6e4b50795750"))
                        monTexte.setText("Beacon JBqt detected " + eddystone.getDistance());
                    else if (eddystone.getInstanceId().equals("756b72646934"))
                        monTexte.setText("Beacon q5nW  detected "  + eddystone.getDistance());
                    Beacon1recognized = true;
                }
                else{
                    monTexte = (TextView)findViewById(R.id.beacon2);
                    if(eddystone.getInstanceId().equals("6e4b50795750"))
                        monTexte.setText("Beacon JBqt detected "  + eddystone.getDistance());
                    else if (eddystone.getInstanceId().equals("756b72646934"))
                        monTexte.setText("Beacon q5nW  detected " + eddystone.getDistance());
                    Beacon1recognized = false;
                }



            }

            @Override
            public void onEddystonesUpdated(List<IEddystoneDevice> eddystones, IEddystoneNamespace namespace) {
                super.onEddystonesUpdated(eddystones, namespace);

            }
        };
    }
}
