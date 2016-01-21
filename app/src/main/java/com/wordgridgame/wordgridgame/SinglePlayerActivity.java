package com.wordgridgame.wordgridgame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class SinglePlayerActivity extends AppCompatActivity {

    GridView letterGrid;

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
    }

}
