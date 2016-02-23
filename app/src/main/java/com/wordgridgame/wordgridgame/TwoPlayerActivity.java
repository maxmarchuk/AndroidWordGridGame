package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TwoPlayerActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);


    }

    public void JoinGame(View view){
        Intent intent =new Intent(this,JoinGameActivity.class);
        startActivity(intent);
    }
    public void CreateGame(View view){
        Intent intent =new Intent(this,CreateGameActivity.class);
        startActivity(intent);
    }
}
