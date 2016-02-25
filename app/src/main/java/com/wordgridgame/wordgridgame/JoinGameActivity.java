package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JoinGameActivity extends Activity {

    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;
    ListView listDevices;
    Button btnJoinGame;
    ArrayAdapter mArrayAdapter;
    Set<BluetoothDevice> setDevice;
    ArrayList deviceList;
    String selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join_game);
        listDevices= (ListView)findViewById(R.id.listViewDevices);
        btnJoinGame=(Button)findViewById(R.id.btnJoin);

        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName=selectedItem;
                String deviceAddress=null;
                for(BluetoothDevice d : setDevice){
                    if(d.getName().equals(deviceName))
                        deviceAddress=d.getAddress();
                }


                    BluetoothDevice remoteDevice=BTAdapter.getRemoteDevice(deviceAddress);

                        ConnectThread t=new ConnectThread(remoteDevice);
                        t.start();



            }
        });

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = new ArrayList();
        setDevice=new HashSet<>();

        mArrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
        listDevices.setAdapter(mArrayAdapter);
        listDevices.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listDevices.setItemChecked(1, true);
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem= parent.getItemAtPosition(position).toString();
            }
        });
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        //Intent discoverableIntent = new
        //        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        //startActivity(discoverableIntent);


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        BTAdapter.startDiscovery();


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {


               System.out.println("on receive");
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device!=null)
                    // Add the name and address to an array adapter to show in a ListView
                    try {

                    setDevice.add(device);
                    deviceList.add(device.getName());
                    mArrayAdapter.notifyDataSetChanged();

                    }catch (Exception ee){System.out.println(ee.toString());}
                }

        }
    };

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("28901242-e667-40eb-bf4d-af5b6555e712"));
            } catch (IOException e) { }
            mmSocket = tmp;

        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            BTAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}