package com.wordgridgame.wordgridgame;

import android.os.Environment;

import java.io.File;
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
    public static Map<String,Integer> GetHiscores(){
        Map<String,Integer> map=new HashMap<>();

        try {
            String temp;
            File sdcard = Environment.getExternalStorageDirectory();
            System.out.println( Environment.getExternalStorageDirectory().toString());
            Scanner file = new Scanner(new File(sdcard, "hiscores.txt"));

            while (file.hasNextLine()) {
                temp = file.nextLine().toUpperCase();
                String[] str=temp.split(",");
                map.put(str[0],Integer.parseInt(str[1]));
            }
            map=sortByValue(map);
            return map;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    //check if player reaches a new high score
    public static Boolean ifNewScore(Integer score){
        Map<String,Integer> map= GetHiscores();
        if(map.get(hiscoresCount-1)<score)
            return true;
        else
            return false;
    }

    //add new score to the hiscores
    public static Map<String,Integer> addNewScore(String userName,Integer score)
    {
        Map<String,Integer> map=new HashMap<String,Integer>();
        map=GetHiscores();
        map.put(userName,score);
        map=sortByValue(map);

        Map<String,Integer> newMap=new LinkedHashMap<String,Integer>();
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>( map.entrySet() );
        for(int i=0;i<hiscoresCount;i++)
        {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;


    }

    private static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

}
