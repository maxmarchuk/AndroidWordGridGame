package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.wordgridgame.wordgridgame.R.layout.activity_help_menu;


public class HelpMenuActivity extends Activity {
    TextView tv;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_help_menu);
        String fontPath = "fonts/fonts.ttf";
        TextView helpTitle = (TextView) findViewById(R.id.txtBoggle);
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        helpTitle.setTypeface(tf);
        tv = (TextView) findViewById(R.id.txtInstruc);
        btnBack = (Button) findViewById(R.id.btnBack);
        tv.setMovementMethod(new ScrollingMovementMethod());
        FontManager fm = new FontManager();
        btnBack.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));

    }
    public void goToMainMenuActivity(View view){
        Intent intent=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(intent);
    }



}

