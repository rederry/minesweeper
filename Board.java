/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * An mutable datatype represent a n*m Minesweeper board
 * Initialize with 10% booms randomly spread on the board, will not change till this game exit.
 */
public class Board {
    
    /** The Board array**/
    private final Square[][] board;
    /** Number of row **/
    private final int n;
    /** Number of col **/
    private final int m;
    
    // Abstraction function: 
    //      Represent a minesweeper game board
    
    // Rep invariant:
    //       board != null, board.length != 0.
    
    // Rep exposure
    //      All fields are private and final
    
    // Thread safety
    //      This is a thread safe datatype, Every operation chang the rep is a synchronized method
    
    public Board(int n, int m) {
        this.n = n;
        this.m = m;
        board = new Square[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Square square = new Square(Math.random() > 0.25 ? false : true, i, j);
                board[i][j] = square;
            }
        }
    }
    
    public Board(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        this.m = Integer.parseInt(line.split(" ")[0]);
        this.n = Integer.parseInt(line.split(" ")[1]);
        board = new Square[n][m];
        for (int i = 0; i < n; i++) {
            String[] tokens = in.readLine().split(" ");
            for (int j = 0; j < m; j++) {
                Square square = new Square(Integer.parseInt(tokens[j]) == 0 ? false : true, i, j);
                board[i][j] = square;
            }
        }
        in.close();
    }
    
    /** Getters */
    public int getRowNum() {
        return n;
    }
    
    public int getColNum() {
        return m;
    }
    
    /**
     * Dig at x col y row, 
     * If out of bounds or this square is not in untouched state then do nothion
     * If square x,y’s state is untouched, change square x,y’s state to dug,
     * If square x,y contains a bomb, change it so that it contains no bomb.
     * 
     * @param x the num of col
     * @param y the num of row
     * @return true if dig a boom else return false.
     */
    public synchronized boolean dig(int x, int y) {
        int tmp = x;
        x = y;
        y = tmp;
        
        if (!inBound(x, y) || !board[x][y].isUntouched()) return false;
        Square square = board[x][y];
        square.setDug();
        if (square.containBoom()) {
            square.setUnBoom();
            System.out.println("dig boom! at " + x + "," + y);
            for (Square s : adjacentSquares(square)) {
                int c = s.adjacentBoomCount();
                if (c != 0)
                    s.setAdjacentBoomCount(c - 1);
            }
            revealAdjacentBoom(square);
            return true;
        } else {
            revealAdjacentBoom(square);
            return false;
        }
    }
    
    /**
     * Flag
     * @param x the num of col
     * @param y the num of row
     */
    public synchronized void flag(int x, int y) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        int tmp = x;
        x = y;
        y = tmp;
        if (inBound(x, y) && board[x][y].isUntouched()) 
            board[x][y].setFlag();
    }
    
    /**
     * DEFLAG
     * @param x the num of col
     * @param y the num of row
     */
    public synchronized void deflag(int x, int y) {
        int tmp = x;
        x = y;
        y = tmp;
        if (inBound(x, y) && board[x][y].isFlagged()) 
            board[x][y].setUntouched();     
    }
    
    /**
     * Print the current board
     */
    public synchronized String lookBoard() {
        String s = "   ";
        for (int k = 0; k < m; k++) { 
            s += String.format("%3d", k);
        }
        s += "\n";
        for (int i = 0; i < n; i++) {
            s += String.format("%3d ", i);
            for (int j = 0; j < m; j++) {
                Square square = board[i][j];
                if (square.isUntouched()) s += " -";
                else if (square.isFlagged()) s += " F";
                else if (square.adjacentBoomCount() == 0) s += "  ";
                else s += " " + square.adjacentBoomCount();
                s += " ";
            }            
            s += "\n";
        }
        return s;
//        String s = "";
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                Square square = board[i][j];
//                if (square.isUntouched()) s += "-";
//                else if (square.isFlagged()) s += "F";
//                else if (square.adjacentBoomCount() == 0) s += " ";
//                else s += square.adjacentBoomCount();
//                if (j != m - 1) s += " ";
//            }            
//            if (i != n - 1) s += "\n";
//        }
//        return s;
    }
    
    @Override
    public synchronized String toString() {
      String s = "   ";
      for (int k = 0; k < m; k++) { 
          s += String.format("%3d", k);
      }
      s += "\n";
      
      for (int i = 0; i < n; i++) {
          s += String.format("%3d ", i);
          for (int j = 0; j < m; j++) {
              Square square = board[i][j];
              if (square.containBoom()) s += " B";
              else s+= " O";
              s += " ";
          }
          s += "\n";
      }
      return s;
    }
    
    /**
     * Test whether the (x, y) in the board
     * @param x
     * @param y
     * @return true if in bound
     */
    private boolean inBound(int x, int y) {
        return x < n && y < m && x >= 0 && y >= 0;
    }
    
    /**
     * Indicating how many adjacent squares contain mines,
     * if no mines are adjacent, the square becomes blank, and all adjacent squares will be recursively revealed.
     * @param square in the board
     */
    private void revealAdjacentBoom(Square square) {
        int boomCount = 0;
        ArrayList<Square>  neighbours = adjacentSquares(square);
        for (Square s : neighbours) 
            if (s.containBoom()) boomCount++;
        square.setDug();
        square.setAdjacentBoomCount(boomCount);
        if (boomCount != 0) return;
        for (Square s : neighbours) {  
            if(s.isUntouched()) revealAdjacentBoom(s);
        }
    }
    
    /**
     * Find all adjacent squares around this square.
     * @param square
     * @return all adjacent squares around this square
     */
    private ArrayList<Square> adjacentSquares(Square square) {
        ArrayList<Square>  neighbours = new ArrayList<Square>();
        int x = square.getX();
        int y = square.getY();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (inBound(x + i, y + j))
                    neighbours.add(board[x+i][y+j]);
            }
        }
        return neighbours;
    }
    
}
