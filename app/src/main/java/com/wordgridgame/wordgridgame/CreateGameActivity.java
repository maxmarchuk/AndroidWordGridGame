package com.wordgridgame.wordgridgame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class CreateGameActivity extends AppCompatActivity {

    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;
    TextView txtViewMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        txtViewMsg=(TextView)findViewById(R.id.txtMsg);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        new AcceptThread().run();


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
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    //TODO work to manage the connection (in a separate thread) and start game
                    txtViewMsg.setText("Waiting for other players");

                    try {
                        mmServerSocket.close();
                    }catch (IOException e) {
                        break;
                    }
                    break;
                }else{
                    txtViewMsg.setText("error socekt==null");
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
