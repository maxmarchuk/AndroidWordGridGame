package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ShakerDemo extends Activity implements Shaker.Callback {
    private Shaker shaker=null;
    private TextView transcript=null;
    private ScrollView scroll=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaker_demo);
        //transcript = (TextView) findViewById(R.id.transcript);
        //scroll=(ScrollView)findViewById(R.id.scroll);
        shaker = new Shaker(this,1.25d, 500, this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        shaker.close();
    }
    public void shakingStarted() {
        Log.d("ShakerDemo", "Shaking started!");
        //transcript.setText(transcript.getText().toString() + "Shaking started\n");
        //scroll.fullScroll(View.FOCUS_DOWN);


    }
    public void shakingStopped() {
        Log.d("ShakerDemo", "Shaking stopped!");
        //transcript.setText(transcript.getText().toString() + "Shaking stopped\n");
        //scroll.fullScroll(View.FOCUS_DOWN);

    }

    public void goToSingleplayer(View v){
        Intent intent = new Intent (getApplicationContext(), SinglePlayerActivity.class);
        startActivity(intent);
    }
}

