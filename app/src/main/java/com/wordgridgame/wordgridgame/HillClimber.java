package com.wordgridgame.wordgridgame;

/**
 * Created by lyf on 2016/1/22.
 * Hill climbing algorithm for generating grid.
 */
public class HillClimber {
    char[][] grid;
    boolean[][] hasBeenHit;
    int wordsCount;
    String soFar;
    void randomGrid(){
        char[][] temp=new char[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++) {
                temp[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
                hasBeenHit[i][j]=false;
            }
        }
        grid=temp;
    }

    private void importList(){
        //TODO import word list
    }

    //traverse the board to get current number of words
    private void traverse(int i,int j){
        if(hasBeenHit[i][j])
            return;

        soFar+=grid[i][j];
        //TODO check if soFar is in word list

        hasBeenHit[i][j]=true;

    }

    //randomly mutate the board
    private char[][] mutate()
    {
        char[][] newGrid=new char[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if((int)(Math.random()*100)<10){
                    newGrid[i][j]= (char) (Math.random() * (90 - 65 + 1) + 65);
                }else{
                    newGrid[i][j]=grid[i][j];
                }
            }
        }
        return newGrid;
    }

    public  char[][] climb()
    {
        randomGrid();
        //begin climb
        while(wordsCount<5) {
            //TODO mutate the board

        }
        return grid;

    }
}
