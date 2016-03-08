package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MultiroundClientActivity extends Activity {
    BluetoothConnectManager clientConnectManager;
    protected static Activity MultiroundClientActivity;

    ArrayAdapter mArrayAdapter;
    ArrayList wordList;
    HashMap<Integer, Integer> scoreMap;
    Board board = null;
    ArrayList<String> letters;
    ArrayList<Integer> buttonsClicked;
    CountDownTimer timer;
    Integer timeLeftInMillis;

    GridLayout letterGrid;
    Button btnBackToMenu;
    Button btnDone;
    ListView mainListView;
    TextView playerScoreTextView;
    TextView timerText;
    TextView currentWordText;
    Dialog waitingDialog;


    Integer GAME_NUMBER;
    boolean HOST_DONE = false;
    boolean CLIENT_DONE = false;
    boolean GAME_DONE = false;

    Integer currentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiround_client);

        initVariables();
        initFonts();
        startGame();
    }

    private void startGame() {
        if (GAME_NUMBER == null) {
            GAME_NUMBER = 0;
        }

        getBoardFromHost();
        new BackgroundGridTask().execute();
        new BluetoothListenerTask().execute();
        playGame();
    }

    private void playGame() {
        CLIENT_DONE = false;
        HOST_DONE = false;

        waitingDialog.hide();
        currentScore = 0;
        wordList.clear();
        timer = createCountDownTimer(timeLeftInMillis);
        timer.start();
    }

    private CountDownTimer createCountDownTimer(Integer cdTime) {
        return new CountDownTimer(cdTime, 1000) {
            public void onTick(long ms) {
                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
            }

            public void onFinish() {
                finishGame();
            }
        };
    }

    private void initVariables() {
        MultiroundClientActivity = this;

        // retrieve all the elements
        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        playerScoreTextView = (TextView) findViewById(R.id.txtPlayerScore);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        timerText = (TextView) findViewById(R.id.txtTimer);
        waitingDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        waitingDialog.setContentView(R.layout.activity_waiting_for_opponent_dialog);

        // initialize variables
        letters = new ArrayList<>();
        wordList = new ArrayList();
        mArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, wordList);
        mainListView.setAdapter(mArrayAdapter);
        buttonsClicked = new ArrayList<>();
        clientConnectManager = new BluetoothConnectManager();
        GAME_NUMBER = null;
        scoreMap = new HashMap<>();
        scoreMap.put(3, 1);
        scoreMap.put(4, 1);
        scoreMap.put(5, 2);
        scoreMap.put(6, 3);
        scoreMap.put(7, 5);
        scoreMap.put(8, 11);

        timeLeftInMillis = 30000; // 30000 is 30 seconds
    }

    public class BluetoothListenerTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            while (!GAME_DONE) {
                if (CLIENT_DONE && HOST_DONE) {
                    MultiroundClientActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Getting board in bluetooth listener");
                            getBoardFromHost();
                            System.out.println("Calling background grid task in btl");
                            new BackgroundGridTask().execute();
                            playGame();
                        }
                    });
                }

                Object tempObj;
                tempObj = clientConnectManager.readObject();

                try {
                    final String message = (String) tempObj;
                    if (message != null) {
                        if (message.equals("roundDone")) {
                            System.out.println("roundDone received from host");
                            MultiroundClientActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedDoneStringFromHost();
                                }
                            });
                        } else if (message.equals("gameDone")) {
                            System.out.println("gameDone received from host");
                            MultiroundClientActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedGameOverFromHost();
                                }
                            });
                        } else {
                            System.out.println("Received an unkown string from host: " + message);
                        }
                    }
                } catch (Exception e) {}

            }
            return null;
        }
    }

    private void receivedGameOverFromHost() {
        Toast.makeText(MultiroundClientActivity.this, "You win! Your opponent's time has run out.", Toast.LENGTH_LONG).show();
        finishGame();

    }

    private void receivedDoneStringFromHost() {
        HOST_DONE = true;
        Toast.makeText(MultiroundClientActivity.this, "Opponent has finished their round.", Toast.LENGTH_SHORT).show();
    }

    private void roundDone() {
        int currentTimeInMillis = getMillisFromTimeString(timerText.getText().toString());
        timeLeftInMillis = ((currentScore * 1000) + currentTimeInMillis);
        timer.cancel();
        sendMessageToHost("roundDone");
        GAME_NUMBER++;
        currentScore = 0;
        playerScoreTextView.setText("0");
        timerText.setText("0:00");

        System.out.println("New Time = " + timeLeftInMillis);

        CLIENT_DONE = true;
    }

    private void sendMessageToHost(String message) {
        clientConnectManager.sendObject(message);
    }

    // Takes time in format of 01:23
    // returns (60 + 23) * 1000
    public Integer getMillisFromTimeString(String time) {
        String[] units = time.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0].replace(" ", "")); //first element
        int seconds = Integer.parseInt(units[1].replace(" ", "")); //second element
        return ((60 * minutes + seconds) * 1000); //add up our values
    }

    private void getBoardFromHost() {
        board = (Board) clientConnectManager.readObject();
        System.out.println("!! Got board from host");
    }

    private void finishGame() {
        timerText.setText("0:00");
        Toast.makeText(MultiroundClientActivity.this, "Time's up! You lose!", Toast.LENGTH_LONG).show();
        sendMessageToHost("gameDone");
//        clientConnectManager.close();
        finish();
    }

    private Integer getPlayerScore() {
        return Integer.parseInt(playerScoreTextView.getText().toString());
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

    public class BackgroundGridTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            adaptBoardToCharList();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Set the gridview's data to new list of letters
                    letterGrid = (GridLayout) findViewById(R.id.gridLayout);

                    for (int i = 0; i < 16; i++) {
                        final Button btn = (Button) letterGrid.getChildAt(i);
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
                    System.out.println("background grid task done");
                }
            });
            return null;
        }
    }

    public void onDoneButtonClick(View v) {
        roundDone();
    }


    private void initFonts() {
        FontManager fm = new FontManager();
        btnBackToMenu.setTypeface(FontManager.getTypeface(getApplicationContext(), fm.FONTAWESOME));
        btnDone.setTypeface(FontManager.getTypeface(getApplicationContext(), fm.FONTAWESOME));
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

    private boolean wordAlreadyAdded(String word) {
        return wordList.contains(word);
    }

    //requires getting score from server
    private void addWord(String word) {
        int currentScore = getPlayerScore();
        int length = word.length();

        if (length <= 8) {
            currentScore += scoreMap.get(length);
            playerScoreTextView.setText(String.valueOf(currentScore));

        } else {
            currentScore += 11;
            playerScoreTextView.setText(String.valueOf(currentScore));
        }

        if (isInDictionary(word)) {
            wordList.add(word);
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
            btn.setBackgroundResource(R.drawable.blueshape);
        }
    }

    private void resetGrid() {
        currentWordText.setText("");
        resetGridCellColors();
        buttonsClicked.clear();
    }

    // assign the global character array representation of the generated letter board

    /****
     * THIS WILL CHANGE
     ****/
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
        return board.dict.contains(word);
    }

    public void goToPreviousActivity(View v) {
        finish();
    }
}
