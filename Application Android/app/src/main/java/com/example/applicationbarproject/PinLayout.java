package com.example.applicationbarproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

public class PinLayout extends AppCompatActivity {

    private static BluetoothDevice device;
    private BluetoothSocket mSocket;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button enter;
    private TextView password;

    private Handler handler;
    private MyBluetoothService mbs;

    private static final String TAG = "BluetoothDeviceConnect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_layout);
        Intent intent = getIntent();
        device = intent.getExtras().getParcelable(MainActivity.EXTRA_BLUETOOTHDEVICE);
        try {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.i(TAG, "Create socket service");
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }

        try {
            mSocket.connect();
            Log.i(TAG, "Connect succesful");
        } catch (IOException e) {
            e.printStackTrace();
        }

        password = findViewById(R.id.pinCode);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "message reçu");
                if(MyBluetoothService.MessageConstants.MESSAGE_READ == msg.what) {
                    String txt = new String((byte[])msg.obj);
                    Log.i(TAG, "Le txt: " + txt);
                    password.setText(txt);
                    this.removeMessages(msg.what, msg.obj);
                }

            }
        };



        enter =  findViewById(R.id.buttonSEND);



        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        Log.e("Device2", device.getName() + " " + device.getAddress());
        mbs = new MyBluetoothService(handler, mSocket);
    }




}
