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
        Intent intent = new Intent(this,BluetoothHostGameActivity.class);
        intent.putExtra("gameType", "basic");
        startActivity(intent);
    }

    public void CreateCutthroatGame(View view){
        Intent intent = new Intent(this,BluetoothHostGameActivity.class);
        intent.putExtra("gameType", "cutthroat");
        startActivity(intent);
    }

    public void CreateMultiroundGame(View view){
        Intent intent = new Intent(this,MultiroundHostActivity.class);
        startActivity(intent);

    }

    public void JoinMultiroundGame(View view){
        Intent intent = new Intent(this,JoinGameActivity.class);
        intent.putExtra("isMultiround", "yes");
        startActivity(intent);
    }
}
