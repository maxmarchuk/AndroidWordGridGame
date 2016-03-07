package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BluetoothClientGameActivity extends Activity {

    //bluetooth stuff
    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;
    BluetoothConnectManager clientConnectManager;

    //gameplay stuff
    GridLayout letterGrid;
    Button btnBackToMenu;
    Button btnDone;
    Button clearButton;
    ListView mainListView;
    TextView player1ScoreTextView;
    TextView player2ScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;
    ArrayList hostWordList;
    HashMap<Integer, Integer> scoreMap;
    HillClimber hc;
    Board board = null;
    TextView currentWordText;
    ArrayList<String> letters;
    TextView timerText;
    ArrayList<Integer> buttonsClicked;
    AlertDialog.Builder usernameBuilder;
    long timeBlinkInMilliSeconds = 60 * 1000;
    Integer currentScore=0;
    protected static Activity BluetoothClientGameActivity;
    Intent gameFinishIntent;
    boolean gameDone = false;
    String gameType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client_game);
        BluetoothClientGameActivity = this;

        //initialize everything
        init();
        new BackgroundGridTask().execute();
        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;
                if (ms < timeBlinkInMilliSeconds) {
//                    timerText.setTextAppearance(getApplicationContext(), R.style.blinkText);
                }
                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
            }

            public void onFinish() {
                timerText.setText("Done");
                currentScore = getPlayer2Score();
                gameEnded();
            }
        }.start();

        // On clicking clear button, empty the current word
        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //resetGrid();
                    }
                }
        );
    }

    private void init() {
        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        // Grab activity elements
        player1ScoreTextView = (TextView) findViewById(R.id.txtPlayer1Score);
        player2ScoreTextView = (TextView) findViewById(R.id.txtPlayer2Score);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        clearButton = (Button) findViewById(R.id.btnClear);

        letters = new ArrayList<>();
        mNameList = new ArrayList();
        hostWordList = new ArrayList();
        mArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mNameList);
        mainListView.setAdapter(mArrayAdapter);
        timerText = (TextView) findViewById(R.id.txtTimer);
        buttonsClicked = new ArrayList<>();


        //read board via bluetooth
        BluetoothConnectManager bcm=new BluetoothConnectManager();
        clientConnectManager = bcm;
        board=(Board)bcm.readObject();
        gameType = (String)bcm.readObject();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++)
            {
                System.out.print(board.board[i][j].letter);
            }
            System.out.println();
        }

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

        initFonts();
        gameFinishIntent =  new Intent(getBaseContext(), GameTwoPlayerDone.class);

    }
    public class BluetoothListenerTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            while(!gameDone) {
                Object tempObj;
                tempObj = clientConnectManager.readObject();

                try{
                    final Integer newScore = (Integer) tempObj;
                    if(newScore != null) {
                        BluetoothClientGameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setHostScore(newScore);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Object not an integer: " + e.getMessage());
                }
                try{
                    final String newWord= (String) tempObj;
                    if(newWord != null) {
                        BluetoothClientGameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addHostWord(newWord);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Object not an String: " + e.getMessage());
                }

            }
            return null;
        }
    }

    private void setHostScore(Integer newScore){
        System.out.println("!!! RECEIVING SCORE: " + newScore.toString());
        player1ScoreTextView.setText(newScore.toString());
    }

    private void addHostWord(String newWord) {
        System.out.println("!!! ADDING NEW WORD: " + newWord);
        hostWordList.add(newWord);
    }

    private void sendNewClientWord(String newWord) {
        System.out.println("!!! SENDING WORD: " + newWord);
        clientConnectManager.sendObject(newWord);
    }

    private void gameEnded(){
        gameDone = true;
        clientConnectManager.close();

        currentScore = getPlayer2Score();

        Integer hostScore = getPlayer1Score();
        gameFinishIntent.putExtra("player1Score", hostScore);
        gameFinishIntent.putExtra("player2Score", currentScore);
        gameFinishIntent.putExtra("foundWords", mNameList);
        gameFinishIntent.putExtra("allWords", board.words);
        startActivity(gameFinishIntent);

        finish();
    }
    private Integer getPlayer2Score(){
        return Integer.parseInt(player2ScoreTextView.getText().toString());
    }

    private Integer getPlayer1Score(){
        return Integer.parseInt(player1ScoreTextView.getText().toString());
    }

    private boolean isValidPick(int index) {
        ArrayList<Integer> validIndices;
        if (buttonsClicked.isEmpty()) {
            return true;
        }
        if (buttonsClicked.contains(index)) {
            return false;
        }
        switch (index % 4) {
            case 0:
                validIndices = new ArrayList<Integer>(Arrays.asList(index - 4, index - 3, index + 1, index + 4, index + 5));
                break;
            case 1:
                validIndices = new ArrayList<Integer>(Arrays.asList(index - 5, index - 4, index - 3, index - 1, index + 1, index + 3, index + 4, index + 5));
                break;
            case 2:
                validIndices = new ArrayList<Integer>(Arrays.asList(index - 5, index - 4, index - 3, index - 1, index + 1, index + 3, index + 4, index + 5));
                break;
            case 3:
                validIndices = new ArrayList<Integer>(Arrays.asList(index - 5, index - 4, index - 1, index + 3, index + 4));
                break;
            default:
                validIndices = new ArrayList<>();
                System.out.println("SHOULD NOT BE HERE");
        }
        if (validIndices.contains(buttonsClicked.get(buttonsClicked.size() - 1))) {
            return true;
        }
        return false;
    }

    public class GenerateWordListTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
//            board.getWords();
            return null;
        }
    }

    public class BackgroundGridTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //hc = new HillClimber(getApplicationContext());
            //board = hc.climb();
            adaptBoardToCharList();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Set the gridview's data to new list of letters
                    letterGrid = (GridLayout) findViewById(R.id.gridLayout);

                    for (int i = 0; i < 16; i++) {
                        final Button btn = (Button) letterGrid.getChildAt(i);
                        btn.setText(letters.get(i));
                        btn.setText(letters.get(i));
                        btn.setTextSize(32.0f);
                        btn.setTypeface(null, Typeface.BOLD);
                        btn.setTag(i);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Integer buttonIndex = (Integer) v.getTag();

                                // If the button is clicked again, attempt to submit the word.
                                if ((buttonsClicked.size() > 0) && (buttonsClicked.get(buttonsClicked.size() - 1) == buttonIndex)) {
                                    String submittedWord = currentWordText.getText().toString();
                                    boolean validWord = valid(submittedWord);

                                    if (validWord) {
                                        addWord(submittedWord);
                                        resetGrid();
                                    }
                                    return;
                                }

                                // If the button is a valid button click (direct neighbor of last button clicked)
                                if (isValidPick(buttonIndex)) {
                                    v.setBackgroundResource(R.drawable.shape);
                                    currentWordText.append(btn.getText());
                                    buttonsClicked.add(buttonIndex);
                                }
                            }
                        });
                    }
                    new BluetoothListenerTask().execute();
                }
            });
            return null;
        }
    }

    public void onDoneButtonClick(View v) {
        finish();
    }


    private void initFonts() {
        FontManager fm = new FontManager();
        btnBackToMenu.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        clearButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        btnDone.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }


    // returns false if the word isn't long enough or isn't in the dictionary of valid words
    private boolean valid(String word) {
        int length = word.length();

        // make sure the word isn't inserted yet
        if (wordAlreadyAdded(word)) {
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

        if (!isInDictionary(word)) {
            Toast.makeText(getApplicationContext(), "Invalid Word", Toast.LENGTH_SHORT).show();
            resetGrid();
            return false;
        }
        return true;
    }

    private boolean wordAlreadyAdded(String word){
        if(gameType.equals("cutthroat")){
            return (mNameList.contains(word) || hostWordList.contains(word));
        } else if (gameType.equals("basic")){
            return mNameList.contains(word);
        }
        else {
            return mNameList.contains(word);
        }
    }

    //requires getting score from server
    private void addWord(String word){
        int currentScore = getPlayer2Score();
        int length = word.length();

        if (length <= 8) {
            currentScore += scoreMap.get(length);
            player2ScoreTextView.setText(String.valueOf(currentScore));
            sendNewClientScore(currentScore);
            sendNewClientWord(word);
        } else {
            currentScore += 11;
            player2ScoreTextView.setText(String.valueOf(currentScore));
            sendNewClientScore(currentScore);
            sendNewClientWord(word);
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
    private void sendNewClientScore(Integer newScore) {
        System.out.println("!!! SENDING SCORE: " + newScore);
        clientConnectManager.sendObject(newScore);
    }

    private void resetGridCellColors() {
        for (int i = 0; i < letterGrid.getChildCount(); i++) {
            Button btn = (Button) letterGrid.getChildAt(i);
            btn.setBackgroundResource(R.drawable.blueshape);
        }
    }

    private void resetGrid() {
        currentWordText.setText("");
        resetGridCellColors();
        buttonsClicked.clear();
    }

    // assign the global character array representation of the generated letter board
    /**** THIS WILL CHANGE ****/
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


    public void goToPreviousActivity(View v){
        finish();
    }
}


