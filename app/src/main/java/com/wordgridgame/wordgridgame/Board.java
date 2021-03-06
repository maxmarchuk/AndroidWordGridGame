package com.wordgridgame.wordgridgame;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import static java.lang.System.out;

/**
 * Created by lyf on 2016/1/25.
 */
public class Board  implements Serializable {
    private static final long serialVersionUID = 7863262235394607247L;
    public Letter[][] board;
    public ArrayList<String> dict=new ArrayList<String>();
    public ArrayList<String> words=new ArrayList<String>();

    class Letter implements Serializable{
        private static final long serialVersionUID = 012L;
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
            if(dict.contains(soFar) && !words.contains(soFar)) {
                if(soFar.length() > 2) {
                    System.out.println(" #####    " + soFar);

                    words.add(soFar);
                }
            }
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

    public Board(char[][] grid, Context context)
    {
        board=new Letter[4][4];
        fromArray(grid);
        try {
            // Open the file
            InputStream fstream = context.getResources().openRawResource(
                    context.getResources().getIdentifier("ospd", "raw", context.getPackageName()));
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                this.dict.add(strLine.toUpperCase());
            }
            br.close();

        } catch(Exception e) {
            System.out.println("Error reading file. Error Message: " + e.getMessage());
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
//        words.clear();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                board[i][j].traverse("");
//                new RunTraversalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i, j);//board[i][j].traverse("");
            }
        }

        return words;
    }

    public class RunTraversalTask extends AsyncTask<Integer, Integer, Void> {
        protected Void doInBackground(Integer... ints) {
            board[ints[0]][ints[1]].traverse("");
            return null;
        }
    }
}
