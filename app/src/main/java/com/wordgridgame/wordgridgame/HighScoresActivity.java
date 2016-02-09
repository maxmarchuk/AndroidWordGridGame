package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HighScoresActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);


        TextView scoreTxt=(TextView)findViewById(R.id.txtScores);
        String hiscores="";
        ArrayList<String> list= PlayerInfoHelper.GetHiscores();
        for(int i=0;i<list.size();i++) {
            String[] temp = list.get(i).split(",");
            hiscores += temp[0]+ " " + temp[1]+"\n";

        }
        scoreTxt.setText(hiscores);
    }
}
