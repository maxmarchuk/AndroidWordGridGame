package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BluetoothClientGameActivity extends Activity {

    //bluetooth stuff
    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;

    //gameplay stuff
    GridLayout letterGrid;
    Button btnBackToMenu;
    Button btnDone;
    Button clearButton;
    ListView mainListView;
    Button playerScoreTextView;
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
    long timeBlinkInMilliSeconds = 60 * 1000;
    protected static Activity BluetoothClientGameActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client_game);

        //initialize everything
        init();
        new BackgroundGridTask().execute();
        new GenerateWordListTask().execute();
        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;
                if (ms < timeBlinkInMilliSeconds) {
                    timerText.setTextAppearance(getApplicationContext(), R.style.blinkText);
                }
                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
            }

            public void onFinish() {
                timerText.setText("Done");
                Integer currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
                if (PlayerInfoHelper.isNewScore(currentScore)) {
                    usernameBuilder.show();
                }
                gameEnded();
                finish();
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
        playerScoreTextView = (Button) findViewById(R.id.txtPlayerScore);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        clearButton = (Button) findViewById(R.id.btnClear);

        letters = new ArrayList<>();
        mNameList = new ArrayList();
        mArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mNameList);
        mainListView.setAdapter(mArrayAdapter);
        timerText = (TextView) findViewById(R.id.txtTimer);
        buttonsClicked = new ArrayList<>();

        //Set up username dialog
        usernameBuilder = new AlertDialog.Builder(this);
        usernameBuilder.setTitle("You reached a new high score! Please enter username!");

        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameBuilder.setView(input);

        // Set up the buttons
        usernameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            //addnewscore if reached a new high score
            public void onClick(DialogInterface dialog, int which) {
                Integer currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
                if (input.getText().length() == 0) {
                    PlayerInfoHelper.currentPlayerName = "Unknown";
                } else {
                    PlayerInfoHelper.currentPlayerName = input.getText().toString();
                }
                PlayerInfoHelper.addNewScore(currentScore);


            }
        });
        usernameBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //read board via bluetooth
        BluetoothConnectManager bcm=new BluetoothConnectManager();
        board=(Board)bcm.readObject();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++)
            {
                System.out.print(board.board[i][j].letter);
            }
            System.out.println();
        }
        bcm.sendData("Board received".getBytes());
        final String message;
        message = new String(bcm.readData());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            }
        });

        //read/send scores via bluetooth





        //timer via bluetooth







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

    }

    private void gameEnded(){
        BluetoothConnectManager bcm=new BluetoothConnectManager();
        Integer hostScore= Integer.parseInt(new String(bcm.readData()));
        Integer currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
        bcm.sendData(currentScore.toString().getBytes());

        if(hostScore>currentScore){
            Toast.makeText(getApplicationContext(), "Host Wins!!!", Toast.LENGTH_SHORT).show();
        }else if(currentScore>hostScore){
            Toast.makeText(getApplicationContext(), "Client Wins", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Tie!!!", Toast.LENGTH_SHORT).show();
        }




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
            board.getWords();
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
                }
            });
            return null;
        }
    }

    public void onDoneButtonClick(View v) {
        Integer currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
        if (PlayerInfoHelper.isNewScore(currentScore)) {
            usernameBuilder.show();
        }
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
        if (mNameList.contains(word)) {
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

    //requires getting score from server
    private void addWord(String word){

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


