package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.wordgridgame.wordgridgame.R.layout.activity_help_menu;


public class HelpMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_help_menu);
        String fontPath = "fonts/fonts.ttf";
        TextView helpTitle = (TextView) findViewById(R.id.txtBoggle);

        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        helpTitle.setTypeface(tf);

    }
    public void goToMainMenuActivity(View view){
        Intent intent=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(intent);
    }

}
