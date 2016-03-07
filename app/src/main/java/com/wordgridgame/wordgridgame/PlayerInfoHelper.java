package com.wordgridgame.wordgridgame;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by lyf on 2016/2/8.
 */
public class PlayerInfoHelper {

    //store top 5 hiscores
    static Integer hiscoresCount=5;
    static String currentPlayerName="default";
    //get hiscores from txt file
    public static ArrayList<String> GetHiscores(Context context){
        ArrayList<String> list=new ArrayList<>();

        try {
            InputStream fstream = context.getResources().openRawResource(
                    context.getResources().getIdentifier("hiscores", "raw", context.getPackageName()));
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                list.add(strLine);
            }
            br.close();

            sortByValue(list);
            return list;
        }catch (Exception e){
            System.out.println("!!!!!!!!!!" + e.getMessage());
            return null;
        }
    }

    //check if player reaches a new high score
    public static Boolean isNewScore(Integer score, Context context){
        ArrayList<String> list= GetHiscores(context);
        for(int i=0;i<list.size();i++)
        {
            int temp =Integer.parseInt(list.get(i).split(",")[1]);
            if(score>temp){
                return true;
            }

        }
        return false;
    }

    //add new score to the hiscores
    public static ArrayList<String> addNewScore(Integer score, Context context)
    {
        ArrayList<String> list= GetHiscores(context);
        //add score to list
        list.add(currentPlayerName+","+score.toString());

        //remove the lowest score
        int lowestIndex=0;
        for(int i=0;i<list.size();i++)
        {
            if(Integer.parseInt(list.get(i).split(",")[1])<Integer.parseInt(list.get(lowestIndex).split(",")[1]))
                lowestIndex=i;
        }
        list.remove(lowestIndex);
        try {
            //read file
            PrintWriter writer = new PrintWriter(new File(PlayerInfoHelper.class.getResource("hiscore").getPath()));
            int i = 0;
            for(i=0; i<list.size();i++)
                System.out.println("HI SCORE: " + list.get(i));
                writer.println(list.get(i));
            writer.close();

        }catch (Exception e){System.out.println(e.toString());}

        return list;


    }

    private  static void sortByValue(ArrayList<String> list){
        for(int i=0;i<list.size();i++){
            for(int j=i+1;j< list.size();j++){
                if(Integer.parseInt(list.get(i).split(",")[1])<Integer.parseInt(list.get(j).split(",")[1])){
                     String temp=list.get(i);
                     list.set(i,list.get(j));
                     list.set(j,temp);
                }
            }
        }


    }

}
