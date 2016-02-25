package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TwoPlayerMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_menu);
    }

    public void goToCutthroat(View view){
        Intent intent=new Intent(getApplicationContext(),CutthroatActivity.class);
        startActivity(intent);
    }
}
