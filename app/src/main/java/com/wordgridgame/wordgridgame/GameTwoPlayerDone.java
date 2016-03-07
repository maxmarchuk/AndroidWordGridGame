package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameTwoPlayerDone extends Activity {

    Button backToMenuButton;
    TextView txtPlayer1Score;
    TextView txtPlayer2Score;
    ListView foundWordList;
    ListView allWordList;

    Integer player1Score;
    Integer player2Score;
    ArrayList foundWords;
    ArrayList allWords;

    ArrayAdapter foundWordsAdapter;
    ArrayAdapter allWordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_two_player_done);
        initElements();
        initFonts();
        initVariables();

    }


    public void goToMainMenuActivity(View view){
        Intent intent=new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
    }

    public void initElements(){
        backToMenuButton = (Button) findViewById(R.id.btnMainMenu);
        txtPlayer1Score = (TextView) findViewById(R.id.txtPlayer1Score);
        txtPlayer2Score = (TextView) findViewById(R.id.txtPlayer2Score);


        foundWordList = (ListView) findViewById(R.id.listFoundWords);
        allWordList = (ListView) findViewById(R.id.listAllWords);
    }

    public void initFonts(){
        backToMenuButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }

    public void initVariables() {
        player1Score = getIntent().getExtras().getInt("player1Score");
        player2Score = getIntent().getExtras().getInt("player2Score");

        foundWords = getIntent().getExtras().getStringArrayList("foundWords");
        allWords = getIntent().getExtras().getStringArrayList("allWords");

        foundWordsAdapter = new ArrayAdapter(getApplicationContext(), R.layout.white_list_item, foundWords);
        allWordsAdapter = new ArrayAdapter(getApplicationContext(), R.layout.white_list_item, allWords);

        txtPlayer1Score.setText(player1Score.toString());
        txtPlayer2Score.setText(player2Score.toString());

        foundWordList.setAdapter(foundWordsAdapter);
        allWordList.setAdapter(allWordsAdapter);

        if (player1Score.equals(player2Score)) {
            Toast.makeText(GameTwoPlayerDone.this, "Tie!", Toast.LENGTH_LONG).show();
        } else if (player1Score > player2Score) {
            Toast.makeText(GameTwoPlayerDone.this, "Player 1 wins!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(GameTwoPlayerDone.this, "Player 2 wins!", Toast.LENGTH_LONG).show();
        }
    }
}
