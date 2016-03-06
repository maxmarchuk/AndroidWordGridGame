package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class OnGameJoinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_game_join);


        //read board via bluetooth
        BluetoothConnectManager bcm=new BluetoothConnectManager();
        Board board=(Board)bcm.readObject();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++)
            {
                System.out.print(board.board[i][j].letter);
            }
            System.out.println();
        }
        bcm.sendData("Board received".getBytes());
        final String message;
        message = new String(bcm.readData());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
