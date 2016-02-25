package com.wordgridgame.wordgridgame;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by lyf on 2016/1/22.
 * Hill climbing algorithm for generating grid.
 */
public class HillClimber {
    Board board;
    int wordsCount;
    char[][] grid;
    Context context;


    public HillClimber(Context context){
        this.context = context;
    }
    void randomGrid(){
        char[][] temp=new char[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++) {
                temp[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
            }
        }
        grid=temp;
    }



    //randomly mutate the board
    private void mutate()
    {
        char[][] temp=board.toArray();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if((int)(Math.random()*100)<10){
                    board.board[i][j].letter  = (char) (Math.random() * (90 - 65 + 1) + 65);
                }
            }
        }
        int newCount=board.getWords().size();
        //recover board if total word count didn't increase
        if(newCount<=wordsCount)
            board.fromArray(temp);
        else
            wordsCount=newCount;


    }

    public Board climb()
    {
        randomGrid();
        board=new Board(grid, this.context);
        //begin climb
        ArrayList<String> list =new ArrayList<String>();

        do{
            list=board.getWords();
        } while(list.size()<5);

        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                System.out.print(" " +board.board[i][j].letter+" ");
            }
            System.out.println();
        }

        return board;

    }
}
