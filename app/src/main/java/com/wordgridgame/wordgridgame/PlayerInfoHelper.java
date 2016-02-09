package com.wordgridgame.wordgridgame;

import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by lyf on 2016/2/8.
 */
public class PlayerInfoHelper {

    //store top 5 hiscores
    static Integer hiscoresCount=5;
    static String currentPlayerName="default";
    //get hiscores from txt file
    public static ArrayList<String> GetHiscores(){
        ArrayList<String> list=new ArrayList<>();

        try {
            String temp;
            File sdcard = Environment.getExternalStorageDirectory();
            Scanner file = new Scanner(new File(sdcard, "hiscores.txt"));
            //Scanner file=new Scanner(new File("D:\\cs554\\havlicek-cs454t4\\hiscores.txt"));
            while (file.hasNextLine()) {
                temp = file.nextLine().toUpperCase();
                list.add(temp);

            }
            sortByValue(list);
            return list;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    //check if player reaches a new high score
    public static Boolean isNewScore(Integer score){
        ArrayList<String> list= GetHiscores();
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
    public static  ArrayList<String> addNewScore(Integer score)
    {
        ArrayList<String> list= GetHiscores();
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
            //
            File sdcard = Environment.getExternalStorageDirectory();
            PrintWriter writer = new PrintWriter(new File(sdcard, "hiscores.txt"));
            for(int i=0;i<list.size();i++)
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
