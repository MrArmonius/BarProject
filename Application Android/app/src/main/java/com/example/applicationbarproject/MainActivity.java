package com.example.applicationbarproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_BLUETOOTHDEVICE = "com.example.applicationbarproject.BLUETOOTHDEVICE";

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static ArrayList<BluetoothDevice> deviceFound = new ArrayList<>();
    private static final String TAG = "BluetoothDeviceFound";
    private BluetoothArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callBT();
        askCOARSE();
        discoverBT();
        afficheListBT();

    }

    private void askCOARSE() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>" +
                                    "<p>For more info see <a href=\"http://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id\">here</a>.</p>"))
                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                REQUEST_ACCESS_COARSE_LOCATION);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }

    private void discoverBT() {
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            Log.i(TAG, "Cancel Discovery");
        }else {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            if (mBtAdapter.startDiscovery()) Log.e(TAG, "Vraaaaaaiiiii");
        }

    }

    private void afficheListBT() {
        adapter = new BluetoothArrayAdapter(this);

        ListView listView = (ListView) findViewById(R.id.list_BT);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PinLayout.class);
                BluetoothDevice sendBT = deviceFound.get(position);
                intent.putExtra(EXTRA_BLUETOOTHDEVICE, sendBT);
                discoverBT();

                startActivity(intent);
            }
        });

    }

    private void callBT() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {

            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void callLocal() {
        boolean gps_enabled = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){}
        if(!gps_enabled) {
            Intent location = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(location);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_CANCELED){
                callBT();
            }
        }

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(!deviceFound.contains(device) && device.getName()!= null)  {
                    deviceFound.add(device);
                    Log.i(TAG, device.getName() + " " + device.getAddress());
                    adapter.add(device);
                }
            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }



}
