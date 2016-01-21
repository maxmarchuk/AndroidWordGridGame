package com.wordgridgame.wordgridgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

    }

    public void goToSinglePlayer(View view) {
        Intent intent = new Intent(MainMenuActivity.this, SinglePlayerActivity.class);
        startActivity(intent);
    }
}
