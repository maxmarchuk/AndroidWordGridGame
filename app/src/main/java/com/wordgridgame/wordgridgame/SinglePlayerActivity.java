package com.wordgridgame.wordgridgame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class SinglePlayerActivity extends AppCompatActivity {

    GridView letterGrid;
    Button submitButton;
    Button clearButton;
    ListView mainListView;
    TextView playerScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;
    HashMap<Integer, Integer> scoreMap;
    HillClimber hc;
    Board board = null;
    char[][] twoDimArray;
    TextView currentWordText;
    ArrayList<String> letters;


    private void init() {
        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        mNameList = new ArrayList();
        hc = new HillClimber();
        letters = new ArrayList<String>();

        // Grab activity elements
        playerScoreTextView = (TextView) findViewById(R.id.txtPlayerScore);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        submitButton = (Button) findViewById(R.id.btnSubmit);
        clearButton = (Button) findViewById(R.id.btnClear);

        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mNameList);
        mainListView.setAdapter(mArrayAdapter);


        scoreMap = new HashMap<Integer, Integer>();
        // Populate the score mapping
        // *Key*: Word Length
        // *Value*: Points
        scoreMap.put(3, 1);
        scoreMap.put(4, 1);
        scoreMap.put(5, 2);
        scoreMap.put(6, 3);
        scoreMap.put(7, 5);
        scoreMap.put(8, 11);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        //initialize everything
        init();

        //Populate the board
        board = hc.climb();
        twoDimArray = board.toArray();

        //Convert the two dimensional array of characters to a list of characters
        for (int i = 0; i < twoDimArray.length; i++) {
            for (int j = 0; j < twoDimArray[i].length; j++) {
                letters.add(String.valueOf(twoDimArray[i][j]));
            }
        }

        //Set the gridview's data to new list of letters
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, letters);
        letterGrid = (GridView) findViewById(R.id.gridView);
        letterGrid.setAdapter(adapter);

        //Set on click listener for each individual letter in the grid
        //TODO: make sure that each letter clicked is a neighbor of the previous one
        letterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(getResources().getColor(R.color.accent));
                currentWordText.append(letters.get(position));
            }
        });

        // Submit Button Click Listener
        // On clicking submit, get the word length and add its respective score to the total score
        //Also add the word to the list of submitted words.
        //TODO: Actually check the words for validity
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num1;
                String submittedWord = currentWordText.getText().toString();
                boolean validWord = addWord(submittedWord);

                // If the words was valid, we know it's already added to the
                // word list so we can clear the current word text
                if (validWord) {
                    currentWordText.setText("");
                    resetGridCellColors();
                }
            }
        });

        // On clicking clear button, empty the current word
        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentWordText.setText("");
                        resetGridCellColors();
                    }
                }
        );

    }

    private boolean addWord(String word) {
        int length = word.length();
        int currentScore = Integer.parseInt(playerScoreTextView.getText().toString());

        // Check word lengths
        if(length == 0){
            Toast.makeText(getApplicationContext(), "Please input a character sequence", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(length <= 2){
            Toast.makeText(getApplicationContext(), "Word must be longer than 2 letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(length <= 8){
            currentScore += scoreMap.get(length);
            playerScoreTextView.setText(String.valueOf(currentScore));
        }
        else {
            currentScore += 11;
            playerScoreTextView.setText(String.valueOf(currentScore));
        }

        //TODO: Check if word is valid

        mNameList.add(word);
        mArrayAdapter.notifyDataSetChanged();

        return true;
    }

    private void resetGridCellColors() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, letters);

        for(int i=0; i < adapter.getCount(); i++){
            View v = letterGrid.getChildAt(i);
            v.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }
}