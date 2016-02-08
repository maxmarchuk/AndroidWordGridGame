package com.wordgridgame.wordgridgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HighScoresActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        Map<String,Integer> scores=PlayerInfoHelper.GetHiscores();
        TextView scoreTxt=(TextView)findViewById(R.id.txtScores);
        String hiscores="";
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>( scores.entrySet() );
        for(int i=0;i<scores.size();i++)
        {
            hiscores+=list.get(i).getKey()+"  "+list.get(i).getValue().toString()+"\n";

        }
        scoreTxt.setText(hiscores);
    }
}
