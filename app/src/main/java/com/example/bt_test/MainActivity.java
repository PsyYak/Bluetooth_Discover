package com.example.bt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBtAdapter;
    private ListView mLvDevices;
    Button btnScan;
    ArrayAdapter<String>arrayAdapter;
    private ArrayList<String> arrayList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        btnScan = findViewById(R.id.btnScan);
        mLvDevices = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        mLvDevices.setAdapter(arrayAdapter);


    }

    public void discoverDevices(View V){
        mBtAdapter.startDiscovery();

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                Log.i("Device",device.getName()+ "\n"+ device.getAddress());
                if(device.getName() == null || device.getName().equals("")){
                    arrayList.add("Unknown Device name"+ "\n"+ device.getAddress());
                }else{
                    arrayList.add(device.getName()+ "\n"+ device.getAddress());
                }

                arrayAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver,intentFilter);
    }
}