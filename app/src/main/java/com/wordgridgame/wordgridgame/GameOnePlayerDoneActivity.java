package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOnePlayerDoneActivity extends Activity {
    Button backToMenuButton;
    TextView gameStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_one_player_done);
        initElements();
        initFonts();
    }

    public void goToMainMenuActivity(View view){
        Intent intent=new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
    }

    public void initElements(){
        backToMenuButton = (Button) findViewById(R.id.btnMainMenu);
        gameStatusText = (TextView) findViewById(R.id.txtGameStatus);
    }

    public void initFonts(){
        backToMenuButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        gameStatusText.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }
}
