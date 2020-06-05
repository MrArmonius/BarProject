package com.example.applicationbarproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    public BluetoothArrayAdapter(Context context) {
        super(context, 0);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BluetoothDevice device = getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_row_bt_name, parent, false);

        }
        if(device != null) {
            TextView btName = convertView.findViewById(R.id.bluetoothName);
            if(device.getName() != null) {
                btName.setText(device.getName());
            }
        }
        return convertView;
    }
}
