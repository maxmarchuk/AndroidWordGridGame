package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MultiroundClientActivity extends Activity {

    Button btnBackToMenu;
    Button btnDone;
    TextView timerText;
    Button playerScoreTextView;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiround_client);


        //initialize everything
        init();
        initFonts();

        timer = new CountDownTimer(5 * 3000, 1000) {

            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;

                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
                timerText.setTextSize(20);
            }

            public void onFinish() {
                finishGame();
            }
        };
        timer.start();



    }
    public void init(){
        //grab elements
        playerScoreTextView = (Button) findViewById(R.id.txtPlayerScore);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        timerText = (TextView) findViewById(R.id.txtTimer);

    }
    private void initFonts() {
        FontManager fm = new FontManager();
        btnBackToMenu.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        btnDone.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }

    private void finishGame(){
        timerText.setText("-:--");
        timer.cancel();

    }
    public void goToPreviousActivity(View view){
        timer.cancel();
        Intent intent=new Intent(getApplicationContext(), BluetoothMenuActivity.class);
        startActivity(intent);
    }

}
