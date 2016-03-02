package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayMainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_main_menu);
    }

    public void goToSinglePlayer(View view) {
        Intent intent = new Intent(this, ShakerDemo.class);
        startActivity(intent);
    }

    public void goToTwoPlayer(View view){
        Intent intent =new Intent(this,TwoPlayerMenuActivity.class);
        startActivity(intent);
    }
    public void goToBluetooth(View view){
        Intent intent =new Intent(this,BluetoothMenuActivity.class);
        startActivity(intent);
    }

}
