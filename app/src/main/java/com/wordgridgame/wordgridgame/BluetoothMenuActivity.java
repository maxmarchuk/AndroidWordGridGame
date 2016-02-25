package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BluetoothMenuActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_menu);


    }

    public void JoinGame(View view){
        Intent intent =new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }
    public void CreateGame(View view){
        Intent intent =new Intent(this,BluetoothHostGameActivity.class);
        startActivity(intent);
    }
}
