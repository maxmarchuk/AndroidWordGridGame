package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static com.wordgridgame.wordgridgame.R.layout.activity_main_menu;

public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main_menu);
    }

    public void goToPlayActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), PlayMainMenuActivity.class);
        startActivity(intent);
    }

}
