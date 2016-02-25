package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothHostGameActivity extends Activity {

    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;
    TextView txtViewMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_host_game);
        txtViewMsg=(TextView)findViewById(R.id.txtCurrentWord);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        //start listening
        new AcceptThread().start();


    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = BTAdapter.listenUsingRfcommWithServiceRecord("cs554team4",UUID.fromString("28901242-e667-40eb-bf4d-af5b6555e712"));
            } catch (IOException e) { System.out.println("listening error:"+e.toString());}
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewMsg.setText("Waiting for player to join");

                        }
                    });

                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("other connected!");
                            Toast.makeText(getApplicationContext(), "other player has connected!",
                                    Toast.LENGTH_LONG).show();

                        }
                    });

                    //TODO work to manage the connection (in a separate thread) and start game
                    try {
                        mmServerSocket.close();
                    }catch (IOException e) {
                        break;
                    }
                }else{
                    txtViewMsg.setText("error socket is null");
                }

            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }
}
