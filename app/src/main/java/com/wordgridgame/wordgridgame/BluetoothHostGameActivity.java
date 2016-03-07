package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothHostGameActivity extends Activity {
    //bluetooth stuff
    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter BTAdapter;
    BluetoothConnectManager hostConnectManager;

    //gameplay stuff
    CountDownTimer gameTimer;
    boolean wordsGenerated = false;
    GridLayout letterGrid;
    Button btnBackToMenu;
    Button btnDone;
    Button clearButton;
    ListView mainListView;
    TextView playerScoreTextView;
    TextView player2ScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;
    ArrayList clientWordList;
    HashMap<Integer, Integer> scoreMap;
    HillClimber hc;
    Board board = null;
    TextView currentWordText;
    ArrayList<String> letters;
    TextView timerText;
    ArrayList<Integer> buttonsClicked;
    AlertDialog.Builder usernameBuilder;
    long timeBlinkInMilliSeconds = 60 * 1000;
    Integer currentScore;
    protected static Activity BluetoothHostGameActivity;
    Intent gameFinishIntent;
    boolean gameDone = false;
    String gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_host_game);
        BluetoothHostGameActivity = this;
        gameType = getIntent().getExtras().getString("gameType");

        //initialize everything
        init();
        initBluetooth();
        new BackgroundGridTask().execute();
//        new GenerateWordListTask().execute();

        gameTimer= new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;
                if (ms < timeBlinkInMilliSeconds) {
                   // timerText.setTextAppearance(getApplicationContext(), R.style.blinkText);
                }
                String text = String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                timerText.setText(text);
            }



            public void onFinish() {
                timerText.setText("Done");
                currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
                gameEnded();
            }
        };
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

    private void gameEnded() {
        gameDone = true;
        hostConnectManager.close();
        currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
//        hostConnectManager.sendObject(currentScore);

//        Integer clientScore = (Integer) hostConnectManager.readObject();

        gameFinishIntent.putExtra("player1Score", currentScore);
        gameFinishIntent.putExtra("player2Score", Integer.valueOf(player2ScoreTextView.getText().toString()));
        gameFinishIntent.putExtra("foundWords", mNameList);
        gameFinishIntent.putExtra("allWords", board.words);
        startActivity(gameFinishIntent);

        finish();
    }

    private void init() {
        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        // Grab activity elements
        playerScoreTextView = (TextView) findViewById(R.id.txtPlayer1Score);
        player2ScoreTextView = (TextView) findViewById(R.id.txtPlayer2Score);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        clearButton = (Button) findViewById(R.id.btnClear);

        letters = new ArrayList<>();
        mNameList = new ArrayList();
        clientWordList = new ArrayList();
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
                PlayerInfoHelper.addNewScore(currentScore, getApplicationContext());


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

        initFonts();
        gameFinishIntent =  new Intent(getBaseContext(), GameTwoPlayerDone.class);
    }

    private void initBluetooth(){
        currentWordText=(TextView)findViewById(R.id.txtCurrentWord);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        //start listening
        new AcceptThread().start();
    }

    private boolean wordAlreadyAdded(String word){
        System.out.println("CLIENT WORD LIST: " + clientWordList);
        if(gameType.equals("cutthroat")){
            boolean contains = (mNameList.contains(word) || clientWordList.contains(word));
            System.out.println("CONTAINS THE WORD: " + contains);
            return contains;
        } else if (gameType.equals("basic")){
            return mNameList.contains(word);
        }
        else {
            return mNameList.contains(word);
        }
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

    private void addWord(String word) {
        int currentScore = Integer.parseInt(playerScoreTextView.getText().toString());
        int length = word.length();

        if (length <= 8) {
            currentScore += scoreMap.get(length);
            playerScoreTextView.setText(String.valueOf(currentScore));
            sendNewHostScore(currentScore);
            sendNewHostWord(word);
        } else {
            currentScore += 11;
            playerScoreTextView.setText(String.valueOf(currentScore));
            sendNewHostScore(currentScore);
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

    private void sendNewHostScore(Integer newScore) {
        System.out.println("!!! SENDING SCORE: " + newScore);
        hostConnectManager.sendObject(newScore);
    }

    private void sendNewHostWord(String newWord) {
        System.out.println("!!! SENDING WORD: " + newWord);
        hostConnectManager.sendObject(newWord);
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

    public void onDoneButtonClick(View v) {
        finish();
    }

    public class GenerateWordListTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            board.getWords();
            System.out.println("!!!!!!!! DONE GENERATING WORDS!");
            return null;
        }
    }

    public class BluetoothListenerTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            while(!gameDone) {
                Object tempObj;
                tempObj = hostConnectManager.readObject();

                try{
                    final Integer newScore = (Integer) tempObj;
                    if(newScore != null) {
                        BluetoothHostGameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setClientScore(newScore);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Object not an integer");
                }

                try{
                    final String newWord= (String) tempObj;
                    if(newWord != null) {
                        BluetoothHostGameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addClientWord(newWord);
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

    private void setClientScore(Integer newScore){
        System.out.println("!!! RECEIVING SCORE: " + newScore.toString());
        player2ScoreTextView.setText(newScore.toString());
    }


    private void addClientWord(String newWord) {
        System.out.println("!!! ADDING NEW CLIENT WORD: " + newWord);
        clientWordList.add(newWord);
    }


    public class BackgroundGridTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            hc = new HillClimber(getApplicationContext());
            board = hc.climb();
            board.getWords();
            adaptBoardToCharList();

            BluetoothHostGameActivity.runOnUiThread(new Runnable() {
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

    private void initFonts() {
        FontManager fm = new FontManager();
        btnBackToMenu.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        clearButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        btnDone.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }

    public void goToPreviousActivity(View v){
        finish();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = BTAdapter.listenUsingRfcommWithServiceRecord("cs554team4",UUID.fromString("28901242-e667-40eb-bf4d-af5b6555e712"));
            } catch (IOException e) { System.out.println("listening error:"+e.toString());}
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentWordText.setText("Waiting for player to join");

                        }
                    });

                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Connection with other player successful", Toast.LENGTH_SHORT).show();
                            currentWordText.setText("");
                        }
                    });
                    BluetoothConnectManager bluetoothConnectManager = new BluetoothConnectManager(socket);
                    hostConnectManager=bluetoothConnectManager;
                    new BluetoothListenerTask().execute();
                    //send board
                    bluetoothConnectManager.sendObject(board);
                    bluetoothConnectManager.sendObject(gameType);

                    for(int i=0;i<4;i++){
                        for(int j=0;j<4;j++)
                        {
                            System.out.print(board.board[i][j].letter);
                        }
                        System.out.println();
                    }

                    gameTimer.start();
                    BluetoothHostGameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentWordText.setText("");
                        }
                    });

                    try {
                        mmServerSocket.close();
                    }catch (IOException e) {
                        break;
                    }
                }else{
                    currentWordText.setText("ERROR: Bluetooth server socket is null");
                }

            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }
}
