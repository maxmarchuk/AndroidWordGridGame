package com.maxmarchuk.boggle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class SinglePlayer extends AppCompatActivity {

    GridView letterGrid;

    static final String[] letters = new String[] {
            "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L",
            "M", "N", "O", "P"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, letters);

        letterGrid = (GridView) findViewById(R.id.gridView);
        letterGrid.setBackgroundColor(Color.parseColor("#a7a7a7a7"));
        letterGrid.setAdapter(adapter);
    }

    private void startGame() {
        // start the timer

        populateGrid();

        //
    }
    private void populateGrid() {
        // import the file?

        // Run the algorithm to return a 4x4 grid of letters

        // populate the GridView (letterGrid) with the letters
    }


    // Start the game timer
    private void startTimer() {

    }

    // Stop the game timer
    private void stopTimer() {

    }

}
