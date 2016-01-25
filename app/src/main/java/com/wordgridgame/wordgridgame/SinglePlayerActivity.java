package com.wordgridgame.wordgridgame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerActivity extends AppCompatActivity {

    GridView letterGrid;
    EditText mainEditText;
    Button submitButton;
    Button clearButton;
    ListView mainListView;
    TextView playerScoreTextView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList = new ArrayList();

    static final String[] letters = new String[]{
            "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L",
            "M", "N", "O", "P"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_player);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, letters);

        letterGrid = (GridView) findViewById(R.id.gridView);
        letterGrid.setBackgroundColor(Color.parseColor("#a7a7a7a7"));
        letterGrid.setAdapter(adapter);
        letterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "You clicked on " + letters[position], Toast.LENGTH_SHORT).show();
            }
        });
        mainEditText =(EditText) findViewById(R.id.main_edit_text);
        playerScoreTextView = (TextView) findViewById(R.id.players_score_textview);
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num1;
                mNameList.add(mainEditText.getText().toString());
                mArrayAdapter.notifyDataSetChanged();
                if (mainEditText.getText().toString().length() == 3) {
                    String regexStr = "^[0-9]*$";
                    if (playerScoreTextView.getText().toString().trim().matches(regexStr)) {
                        String s1 = playerScoreTextView.getText().toString();
                        num1 = Integer.parseInt(s1);
                        num1 = num1 + 1;
                        playerScoreTextView.setText(Integer.toString(num1));
                    } else {
                        playerScoreTextView.setText(Integer.toString(1));
                    }

                } else if (mainEditText.getText().toString().length() == 5) {
                    String regexStr = "^[0-9]*$";
                    if (playerScoreTextView.getText().toString().trim().matches(regexStr)) {
                        String s1 = playerScoreTextView.getText().toString();
                        num1 = Integer.parseInt(s1);
                        num1 = num1 + 2;
                        playerScoreTextView.setText(Integer.toString(num1));
                    } else {
                        playerScoreTextView.setText(Integer.toString(2));
                    }

                } else if (mainEditText.getText().toString().length() == 6) {
                    String regexStr = "^[0-9]*$";
                    if (playerScoreTextView.getText().toString().trim().matches(regexStr)) {
                        String s1 = playerScoreTextView.getText().toString();
                        num1 = Integer.parseInt(s1);
                        num1 = num1 + 3;
                        playerScoreTextView.setText(Integer.toString(num1));
                    } else {
                        playerScoreTextView.setText(Integer.toString(3));
                    }

                } else if (mainEditText.getText().toString().length() == 7) {
                    String regexStr = "^[0-9]*$";
                    if (playerScoreTextView.getText().toString().trim().matches(regexStr)) {
                        String s1 = playerScoreTextView.getText().toString();
                        num1 = Integer.parseInt(s1);
                        num1 = num1 + 5;
                        playerScoreTextView.setText(Integer.toString(num1));
                    } else {
                        playerScoreTextView.setText(Integer.toString(5));
                    }

                } else if (mainEditText.getText().toString().length() > 7) {
                    String regexStr = "^[0-9]*$";
                    if (playerScoreTextView.getText().toString().trim().matches(regexStr)) {
                        String s1 = playerScoreTextView.getText().toString();
                        num1 = Integer.parseInt(s1);
                        num1 = num1 + 11;
                        playerScoreTextView.setText(Integer.toString(num1));
                    } else {
                        playerScoreTextView.setText(Integer.toString(11));
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Not a long enough word!", Toast.LENGTH_LONG).show();
                }


            }
        });

            clearButton=(Button)

            findViewById(R.id.clear_button);

            clearButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                int count = mArrayAdapter.getCount();
                mArrayAdapter.remove(mArrayAdapter.getItem((count - 1)));
                mArrayAdapter.notifyDataSetChanged();
            }
            }

            );
            mainListView=(ListView) findViewById(R.id.main_list_view);

            mArrayAdapter=new ArrayAdapter(this,
                         android.R.layout.simple_list_item_1,
                         mNameList);

// Set the ListView to use the ArrayAdapter
            mainListView.setAdapter(mArrayAdapter);



    }


    }
