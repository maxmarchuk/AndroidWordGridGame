package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MultiroundHostActivity extends Activity {
    //bluetooth stuff
    public static int REQUEST_BLUETOOTH = 1;
    public static int DEFAULT_TIMER_VALUE_MILLISECONDS = 30000;
    BluetoothAdapter BTAdapter;
    BluetoothConnectManager hostConnectManager;

    //gameplay stuff
    CountDownTimer timer;
    boolean wordsGenerated = false;
    GridLayout letterGrid;
    Button btnBackToMenu;
    Button btnDone;
    ListView mainListView;
    TextView playerScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList wordList;
    HashMap<Integer, Integer> scoreMap;
    HillClimber hc;
    Board board = null;
    TextView currentWordText;
    ArrayList<String> letters;
    TextView timerText;
    ArrayList<Integer> buttonsClicked;
    Integer currentScore;
    protected static Activity MultiroundHostActivity;
    boolean CLIENT_CONNECTED = false;
    boolean CLIENT_DONE = false;
    boolean HOST_DONE = false;
    boolean GAME_DONE = false;
    Integer GAME_NUMBER = null;
    Integer timeLeftInMillis;
    Dialog waitingDialog;
    BackgroundGridTask bgGridTask;


    String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiround_host);
        MultiroundHostActivity = this;

        init();
        initBluetooth();

        startGame();
    }

    private void startGame() {
        if (GAME_NUMBER == null) {
            GAME_NUMBER = 0;
        }

        bgGridTask = new BackgroundGridTask();
        bgGridTask.execute();

        waitingDialog.show();
    }

    private void playGame() {
        System.out.println("PLAYING GAME");
        CLIENT_DONE = false;
        HOST_DONE = false;

        waitingDialog.hide();
        currentScore = 0;
        wordList.clear();
        timer = createCountDownTimer(timeLeftInMillis);
        timer.start();
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
            wordList.add(word);
            mArrayAdapter.notifyDataSetChanged();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Invalid word!", Toast.LENGTH_SHORT).show();
            resetGrid();
            return;
        }

    }

    private boolean isInDictionary(String word) {
        return board.dict.contains(word);
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


    public class BackgroundGridTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            hc = new HillClimber(getApplicationContext());
            board = hc.climb();
            System.out.println("created a new board in bggt");
            adaptBoardToCharList();

            MultiroundHostActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    generateBoardLetters();
                }
            });
            return null;
        }
    }

    private void generateBoardLetters() {
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
    }

    private boolean wordAlreadyAdded(String word) {
        return wordList.contains(word);
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


    private void init() {
        timeLeftInMillis = DEFAULT_TIMER_VALUE_MILLISECONDS;

        timer = createCountDownTimer(timeLeftInMillis);

        currentWordText = (TextView) findViewById(R.id.txtCurrentWord);
        // Grab activity elements
        playerScoreTextView = (TextView) findViewById(R.id.txtPlayerScore);
        mainListView = (ListView) findViewById(R.id.listSubmittedWords);
        btnBackToMenu = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);

        waitingDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        waitingDialog.setContentView(R.layout.activity_waiting_for_opponent_dialog);
        letters = new ArrayList<>();
        wordList = new ArrayList();
        mArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, wordList);
        mainListView.setAdapter(mArrayAdapter);
        timerText = (TextView) findViewById(R.id.txtTimer);
        buttonsClicked = new ArrayList<>();

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
    public void onDoneButtonClick(View v){
        roundDone();
    }

    private void sendMessageToClient(String message) {
        hostConnectManager.sendObject(message);
    }

    private void initBluetooth() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        //start listening
        new AcceptThread().start();
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

    private void initFonts() {
        FontManager fm = new FontManager();
        btnBackToMenu.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        btnDone.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
    }

    public class BluetoothListenerTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            while(!GAME_DONE) {
                if (CLIENT_DONE && HOST_DONE) {
                    MultiroundHostActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Client and host done. playing game.");
                            sendBoard();
                            System.out.println("Sent Board to Client");
                            playGame();
                        }
                    });
                }

                Object tempObj;
                tempObj = hostConnectManager.readObject();
                try{
                    System.out.println("object read, attempting casting");
                    final String message= (String) tempObj;
                    if(message != null) {
                        if(message.equals("roundDone")) {
                            System.out.println("roundDone received from client");
                            MultiroundHostActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedDoneStringFromClient();
                                }
                            });
                        } else if(message.equals("gameDone")){
                            System.out.println("gameDone received from client");
                            MultiroundHostActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedGameOverFromClient();
                                }
                            });
                        } else if(message.equals("clientConnected")){
                            System.out.println("client connected!");
                            MultiroundHostActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedClientConnectMessage();
                                }
                            });
                        } else {
                            System.out.println("Received an unknown string from client: " + message);
                        }
                    }
                } catch (Exception e) {}
            }
            return null;
        }
    }

    private void receivedClientConnectMessage() {
        System.out.println("Client connected message received");
        Toast.makeText(MultiroundHostActivity.this, "Client has connected.", Toast.LENGTH_SHORT).show();
        sendBoard();
        playGame();
    }

    private void receivedGameOverFromClient() {
        Toast.makeText(MultiroundHostActivity.this, "You win! Your opponent's time has run out.", Toast.LENGTH_LONG).show();
    }

    private void receivedDoneStringFromClient() {
        CLIENT_DONE = true;
        Toast.makeText(MultiroundHostActivity.this, "Opponent has finished their round.", Toast.LENGTH_SHORT).show();
    }

    private void sendBoard() {
        System.out.println("sending board to client");
        hostConnectManager.sendObject(board);
    }

    private void finishGame() {
        timerText.setText("0:00");
        Toast.makeText(MultiroundHostActivity.this, "Time's up! You lose!", Toast.LENGTH_LONG).show();
        sendMessageToClient("gameDone");
        finish();
    }

    private void roundDone() {
        this.hc = new HillClimber(getApplicationContext());
        this.board = hc.climb();
        adaptBoardToCharList();
        generateBoardLetters();

        timer.cancel();
        sendMessageToClient("roundDone");

        int currentTimeInMillis = getMillisFromTimeString(timerText.getText().toString());
        timeLeftInMillis = ((currentScore * 1000) + currentTimeInMillis);
        currentScore = 0;
        playerScoreTextView.setText("0");
        timerText.setText("0:00");


        System.out.println("New Time = " + timeLeftInMillis);

        GAME_NUMBER++;
        HOST_DONE = true;

        waitingDialog.show();
    }

    // Takes time in format of 01:23
    // returns (60 + 23) * 1000
    public Integer getMillisFromTimeString(String time) {
        String[] units = time.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0].replace(" ", "")); //first element
        int seconds = Integer.parseInt(units[1].replace(" ", "")); //second element
        return ((60 * minutes + seconds) * 1000); //add up our values
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = BTAdapter.listenUsingRfcommWithServiceRecord("cs554team4", UUID.fromString("28901242-e667-40eb-bf4d-af5b6555e712"));
            } catch (IOException e) {
                System.out.println("listening error:" + e.toString());
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    BluetoothConnectManager.mmSocket = socket;
                    hostConnectManager = new BluetoothConnectManager();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Connection with other player successful", Toast.LENGTH_SHORT).show();
                            receivedClientConnectMessage();
                            new BluetoothListenerTask().execute();
                        }
                    });

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        break;
                    }
                } else {
                    currentWordText.setText("ERROR: Bluetooth server socket is null");
                }

            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
