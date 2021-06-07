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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBtAdapter;
    private ListView mLvDevices;
    private Button btnScan,btnPair;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String> arrayList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare variables and connect with layout
        btnScan = findViewById(R.id.btnScan);
        btnPair = findViewById(R.id.btnPair);
        mLvDevices = findViewById(R.id.list);
        // Get default device adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Assign new arrayList
        arrayList = new ArrayList<>();
        // Assign new Adapter
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        // Set Adapter to Listview
        mLvDevices.setAdapter(arrayAdapter);


    }

    /**
     * OnClick function, in layout XML you will notice the "onClick" and this function name
     * this can also be replace with regular onClick listener inside the onCreate
     * @param V the button that was clicked ( if using multiple button you will need to have switch case here
     *          in order to manage the different button and their behavior.
     */
    public void discoverDevices(View V){

        // if Adapter is null meaning the device is not supported with a BT device there for this won't work so instead of crash, simple toast to user
        if(mBtAdapter == null){
            Toast.makeText(this,"Device does not support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        // if Adapter is not null, start device discovery using the device BT
        else {
            // Clear the list if it has any value ( like from previous content )
            if(arrayList.size()>0) {
                arrayList.clear();
            }
            mBtAdapter.startDiscovery();
        }

    }
    public void getPairList(View v){

        if(mBtAdapter==null){
            Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
        }
        else{
            if(arrayList.size()>0){
                arrayList.clear();
            }

            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            if(pairedDevices.size()>0){
                for(BluetoothDevice device: pairedDevices){
                     arrayList.add("Name: "+device.getName() +"MAC Address: "+device.getAddress());
                }
            }
        }
    }





    // Broadcast receiver is used to communicate with the device components and use them for our need ( discover near by BT devices / list connected devices)
     BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Declare the action received from the onStart method that we override
            String action = intent.getAction();


            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // Get the device founded near by
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                // Log for us to check what we get
                Log.i("Device",device.getName()+ "\n"+ device.getAddress());
                // If device name is null replace with a hardcoded sting
                if(device.getName() == null || device.getName().equals("")){

                    arrayList.add("Unknown Device name"+ "\n"+ device.getAddress());
                }else{
                    arrayList.add(device.getName()+ "\n"+ device.getAddress());
                }
                // Notify the adapter on change and refresh list
                arrayAdapter.notifyDataSetChanged();
            }

            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                // Log for us to check what we get
                Log.i("Device",device.getName()+ "\n"+ device.getAddress()+"\n"+ device.getBondState());

                if(device.getName() == null || device.getName().equals("")){

                    arrayList.add("Unknown Device name"+ "\n"+ device.getAddress()+"\n"+ device.getBondState());
                }else{
                    arrayList.add(device.getName()+ "\n"+ device.getAddress()+"\n"+ device.getBondState());
                }
                arrayAdapter.notifyDataSetChanged();
            }


        }
    };

    /**
     * Override to the onStart method to register our Broadcast Receiver to always work.
     */
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    /**
     * If we close the application, kill the Broadcast Receiver.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}