package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GameOnePlayerDoneActivity extends Activity {
    Button backToMenuButton;
    TextView txtScore;
    ListView foundWordList;
    ListView allWordList;

    Integer score;
    ArrayList foundWords;
    ArrayList allWords;

    ArrayAdapter foundWordsAdapter;
    ArrayAdapter allWordsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_one_player_done);
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
        txtScore = (TextView) findViewById(R.id.txtScore);

        foundWordList = (ListView) findViewById(R.id.listFoundWords);
        allWordList = (ListView) findViewById(R.id.listAllWords);
    }

    public void initFonts(){
        backToMenuButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }

    public void initVariables() {
        score = getIntent().getExtras().getInt("score");
        foundWords = getIntent().getExtras().getStringArrayList("foundWords");
        allWords = getIntent().getExtras().getStringArrayList("allWords");

        foundWordsAdapter = new ArrayAdapter(getApplicationContext(), R.layout.white_list_item, foundWords);
        allWordsAdapter = new ArrayAdapter(getApplicationContext(), R.layout.white_list_item, allWords);

        txtScore.setText(score.toString());
        foundWordList.setAdapter(foundWordsAdapter);
        allWordList.setAdapter(allWordsAdapter);
    }
}