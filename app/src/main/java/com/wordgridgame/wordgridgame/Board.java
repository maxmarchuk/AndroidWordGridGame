package com.wordgridgame.wordgridgame;

import android.os.Environment;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

/**
 * Created by lyf on 2016/1/25.
 */
public class Board {
    class Letter {
        public char letter;
        public int X;
        public int Y;
        public boolean hasBeenHit;
        public Letter(char letter,int X,int Y)
        {
            this.letter=letter;
            this.X=X;
            this.Y=Y;
            this.hasBeenHit=false;
        }

        public void traverse(String soFar)
        {
            System.out.println("TRAVERSING");
            if(words.size() > 5)
                return;
            if(hasBeenHit)
                return;
            soFar+=this.letter;
            //check if any word in the list begins with soFar
            boolean startWith=false;
            for(int i=0;i<dict.size();i++)
            {
                if(dict.get(i).startsWith(soFar)){
                    startWith=true;
                    break;
                }
            }
            if(!startWith)
                return;
            if(dict.contains(soFar) && !words.contains(soFar))
                words.add(soFar);
            hasBeenHit=true;
            //traverse letter above
            if(Y>0)
                board[X][Y-1].traverse(soFar);
            //letter below
            if(Y<3)
                board[X][Y+1].traverse(soFar);
            //letter left
            if(X>0)
                board[X-1][Y].traverse(soFar);
            //letter right
            if(X<3)
                board[X+1][Y].traverse(soFar);
            //letter up-left
            if(X>0 && Y>0)
                board[X-1][Y-1].traverse(soFar);
            //letter up-right
            if(X<3 && Y>0)
                board[X+1][Y-1].traverse(soFar);
            //down-left
            if(X>0 && Y<3)
                board[X-1][Y+1].traverse(soFar);
            //down-right
            if(X<3 && Y<3)
                board[X+1][Y+1].traverse(soFar);
            hasBeenHit=false;
        }
    }
    public Letter[][] board;
    private ArrayList<String> dict=new ArrayList<String>();
    public ArrayList<String> words=new ArrayList<String>();
    public Board(char[][] grid,String wordListPath)
    {
        board=new Letter[4][4];
        fromArray(grid);
        try {
            String temp;
            File sdcard = Environment.getExternalStorageDirectory();

            Scanner file = new Scanner(new File(sdcard, "ospd.txt"));
            while (file.hasNextLine()) {
                temp = file.nextLine().toUpperCase();
                this.dict.add(temp);
            }
        }catch (Exception e){
            System.out.println("File not found " + e.getMessage());
        }
    }

    public char[][] toArray()
    {
        char[][] arr =new char[4][4];
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                arr[i][j]=board[i][j].letter;
            }
        return arr;
    }
    public void fromArray(char[][] arr)
    {
        for(int i=0;i<4;i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = new Letter(arr[i][j],i,j);
            }
        }
    }

    //return current words
    public ArrayList<String> getWords(){
        words.clear();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                board[i][j].traverse("");

            }
        }
        return words;

    }

}
