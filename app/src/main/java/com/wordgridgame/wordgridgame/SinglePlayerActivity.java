package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;


public class SinglePlayerActivity extends Activity {

    GridLayout letterGrid;
    Button submitButton;
    Button clearButton;
    ListView mainListView;
    TextView playerScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;
    HashMap<Integer, Integer> scoreMap;
    HillClimber hc;
    Board board = null;
    TextView currentWordText;
    ArrayList<String> letters;
    TextView timerText;
    ArrayList<Integer> buttonsClicked;
    AlertDialog.Builder usernameBuilder;
    long timeBlinkInMilliSeconds = 60*1000;


    private void init() {
        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        mNameList = new ArrayList();
        hc = new HillClimber();
        letters = new ArrayList<>();

        // Grab activity elements
        playerScoreTextView = (TextView) findViewById(R.id.txtPlayerScore);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        submitButton = (Button) findViewById(R.id.btnSubmit);
        clearButton = (Button) findViewById(R.id.btnClear);

        mArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mNameList);
        mainListView.setAdapter(mArrayAdapter);
        timerText = (TextView) findViewById(R.id.txtTimer);
        buttonsClicked = new ArrayList<>();

        //Set up username dialog
        usernameBuilder = new AlertDialog.Builder(this);
        usernameBuilder.setTitle("You reaced a new hiscore! Please enter username!");

        final EditText input = new EditText(this);
       // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameBuilder.setView(input);

       // Set up the buttons
        usernameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            //addnewscore if reached a new high score
            public void onClick(DialogInterface dialog, int which) {
                Integer currentScore= Integer.parseInt(playerScoreTextView.getText().toString());
                PlayerInfoHelper.currentPlayerName = input.getText().toString();
                PlayerInfoHelper.addNewScore(currentScore);


            }
        });
        usernameBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        // Populate the score mapping
        // *Key*: Word Length
        // *Value*: Points
        scoreMap = new HashMap<Integer, Integer>();
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

        adaptBoardToCharList();

        new CountDownTimer(5*60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;
                if(ms<timeBlinkInMilliSeconds){
                    timerText.setTextAppearance(getApplicationContext(), R.style.blinkText);
                }
                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
            }

            public void onFinish() {
                timerText.setText("Time's Up!");
                Integer currentScore= Integer.parseInt(playerScoreTextView.getText().toString());
                if(PlayerInfoHelper.isNewScore(currentScore)) {
                    usernameBuilder.show();
                }


            }
        }.start();

        //Set the gridview's data to new list of letters
        letterGrid = (GridLayout) findViewById(R.id.gridLayout);

        for (int i = 0; i < letterGrid.getChildCount(); i++) {
            final Button btn = (Button) letterGrid.getChildAt(i);
            btn.setText(letters.get(i));
            btn.setTag(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer buttonIndex = (Integer)v.getTag();

                    //if the button hasn't been clicked yet
                    if(!buttonsClicked.contains(buttonIndex)) {
                        if(isValidPick(buttonIndex)){
                            v.setBackgroundResource(R.drawable.yellowbutton9patch);
                            currentWordText.append(btn.getText());
                            buttonsClicked.add(buttonIndex);
                        }
                    }
                }
            });
        }


        // Submit Button Click Listener
        // On clicking submit, get the word length and add its respective score to the total score
        //Also add the word to the list of submitted words.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String submittedWord = currentWordText.getText().toString();
                boolean validWord = valid(submittedWord);

                // If the words was valid, we know it's already added to the
                // word list so we can clear the current word text
                if (validWord) {
                    addWord(submittedWord);
                    resetGrid();
                }
            }
        });

        // On clicking clear button, empty the current word
        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetGrid();
                    }
                }
        );

    }

    // returns false if the word isn't long enough or isn't in the dictionary of valid words
    private boolean valid(String word) {
        int length = word.length();

        // make sure the word isn't inserted yet
        if(mNameList.contains(word)){
            Toast.makeText(getApplicationContext(), "Word already added", Toast.LENGTH_SHORT).show();
            resetGrid();
            return false;
        }
        // Check word lengths
        if (length == 0) {
            Toast.makeText(getApplicationContext(), "Please input a character sequence", Toast.LENGTH_SHORT).show();
            return false;
        } else if (length <= 2) {
            Toast.makeText(getApplicationContext(), "Word must be longer than 2 letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!isInDictionary(word)){
            Toast.makeText(getApplicationContext(), "Invalid Word", Toast.LENGTH_SHORT).show();
            resetGrid();
            return false;
        }
        return true;
    }

    private void addWord(String word) {
        int currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
        int length = word.length();

        if (length <= 8) {
            currentScore += scoreMap.get(length);
            playerScoreTextView.setText(String.valueOf(currentScore));
        } else {
            currentScore += 11;
            playerScoreTextView.setText(String.valueOf(currentScore));
        }

        if (isInDictionary(word)) {
            mNameList.add(word);
            mArrayAdapter.notifyDataSetChanged();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Invalid word!", Toast.LENGTH_SHORT).show();
            resetGrid();
            return;
        }

    }

    private void resetGridCellColors() {
        for (int i = 0; i < letterGrid.getChildCount(); i++) {
            Button btn = (Button) letterGrid.getChildAt(i);
            btn.setBackgroundResource(R.drawable.bluebutton9patch);
        }
    }

    private void resetGrid() {
        currentWordText.setText("");
        resetGridCellColors();
        buttonsClicked.clear();
    }


    // assign the global character array representation of the generated letter board
    private void adaptBoardToCharList() {
        char[][] twoDimArray = board.toArray();

        //Convert the two dimensional array of characters to a list of characters
        for (int i = 0; i < twoDimArray.length; i++) {
            for (int j = 0; j < twoDimArray[i].length; j++) {
                letters.add(String.valueOf(twoDimArray[i][j]));
            }
        }
    }

    private boolean isInDictionary(String word) {
        return board.words.contains(word);
    }

    private boolean isValidPick(int index){
        ArrayList<Integer> validIndices;
        if(buttonsClicked.isEmpty()){
            return true;
        }
        switch(index%4){
            case 0:
                validIndices = new ArrayList<Integer>(Arrays.asList(index-4, index-3, index+1, index+4, index+5));
                break;
            case 1:
                validIndices = new ArrayList<Integer>(Arrays.asList(index-5, index-4, index-3, index-1, index+1, index+3, index+4, index+5));
                break;
            case 2:
                validIndices = new ArrayList<Integer>(Arrays.asList(index-5, index-4, index-3, index-1, index+1, index+3, index+4, index+5));
                break;
            case 3:
                validIndices = new ArrayList<Integer>(Arrays.asList(index-5, index-4, index-1, index+3, index+4));
                break;
            default:
                validIndices = new ArrayList<>();
                System.out.println("SHOULD NOT BE HERE");
        }
        if(validIndices.contains(buttonsClicked.get(buttonsClicked.size()-1))){
            return true;
        }
        return false;
    }
}