package chess;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class ChessEngine {
    
    private String[][] board = new String[8][8];
    
    private static Map<String,Integer> whitePieceVals = new HashMap<String, Integer>();
    private static Map<String,Integer> blackPieceVals = new HashMap<String, Integer>();
    
    private static int[][] queenMap = new int[8][8];
    private static int[][] rookMap = new int[8][8];
    private static int[][] knightMap = new int[8][8];
    private static int[][] bishopMap = new int[8][8];
    private static int[][] pawnMap = new int[8][8];
    
    
    public ChessEngine() {
        
        initializeBoard();
        initializePieceVals();
        initializePieceMaps();
        //Heuristics
        
        //King in check?
        //King checkMate - / + 100000
        
        //Weighted sum of pieces 
        //pieces weighted by position and value
        //Queen - 9   middle
        //Rook - 5    middle, advance to opp row 2
        //Knight - 3  middle, avoid corners
        //Bishop - 3  middle, avoid corners
        //pawn - 1    advance
        
        //
    }
    
    public boolean userMove(int x0, int y0, int x1, int y1) {
        if (!whitePieceVals.containsKey(board[y0][x0])) return false;
        if (whitePieceVals.containsKey(board[y1][x1])) return false;
        if (!isValidMove(x0,y0,x1,y1,board[y0][x0])) return false;
        
        //evaluate move
        
        board[y1][x1] = board[y0][x0];
        board[y0][x0] = null;
        
        return true;
    }
    
    public int[][] agentMove() {
        int[][] move = new int[2][2];
        move = miniMax();
        int y0 = move[0][0], x0 = move[0][1], y1 = move[1][0], x1 = move[1][1];
        
        //evaluate move
        
        //board[y1][x1] = board[y0][x0];
        //board[y0][x0] = null;
        
        return move;
    }
    
    public int[][] miniMax() {
        int[][] result = {{1,0},{2,0}};
        return result;
    }
    
    private boolean isValidMove(int x0, int y0, int x1, int y1, String piece) {
        char p = piece.charAt(1);
        if (p == 'Q') return validRook(x0,y0,x1,y1) || validBishop(x0,y0,x1,y1);
        if (p == 'R') return validRook(x0,y0,x1,y1);
        if (p == 'K') return validKnight(x0,y0,x1,y1);
        if (p == 'B') return validBishop(x0,y0,x1,y1);
        if (p == 'P') return validPawn(x0,y0,x1,y1);
        if (p == 'k') return validKing(x0,y0,x1,y1);
        return false;
    }
    
    private boolean validKing(int x0, int y0, int x1, int y1) {
        int dy = Math.abs(y1 - y0), dx = Math.abs(x1 - x0);
        return dy < 2 && dx < 2 && dy + dx > 0;
    }

    private boolean validPawn(int x0, int y0, int x1, int y1) {
        int dy = y1 - y0, dx = x1 - x0;
        return dy == -1 && dx == 0;
    }

    private boolean validKnight(int x0, int y0, int x1, int y1) {
        int dy = Math.abs(y1 - y0), dx = Math.abs(x1 - x0);
        return (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
    }

    private boolean validBishop(int x0, int y0, int x1, int y1) {
        int dy = y1 - y0, dx = x1 - x0;
        if (Math.abs(dy) != Math.abs(dx)) return false;
        for (int i = y0; i != y1 && i % 8 != 0; i += dy / Math.abs(dy)) {
            int j = x0 + Math.abs(i-y0) * (dx / Math.abs(dx));
            if (i != y0 && board[i][j] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean validRook(int x0, int y0, int x1, int y1) {
        System.out.println("here");
        int dy = y1 - y0, dx = x1 - x0;
        if (dy == 0) {
            for (int i = x0; i != x1 && i != 8; i += dx / Math.abs(dx)) {
                System.out.println(i);
                if (i != x0 && board[y0][i] != null) return false;
            }
            return true;
        } else if (dx == 0) {
            for (int i = y0; i != y1 && i != 8; i += dy / Math.abs(dy)) {
                System.out.println(i);
                if (i != y0 && board[i][x0] != null) return false;
            }
            return true;
        }
        return false;
    }

    private void initializeBoard() {
        String[] r1 = {"R","K","B","Q","k","B","K","R"};
        for (int i = 0; i < 8; i++) {
            System.out.println(i);
            board[0][i] = "B" + r1[i];
            board[1][i] = "BP";
            board[7][i] = "W" + r1[i];
            board[6][i] = "WP";
        }
    }
    private void initializePieceVals() {
        whitePieceVals.put("WQ",9); whitePieceVals.put("WR",5); whitePieceVals.put("WK",3);
        whitePieceVals.put("WB",3); whitePieceVals.put("WP",3); whitePieceVals.put("Wk",100);
        
        blackPieceVals.put("WQ",9); blackPieceVals.put("WR",5); blackPieceVals.put("WK",3);
        blackPieceVals.put("WB",3); blackPieceVals.put("WP",3); blackPieceVals.put("Wk",100);
        
    }
    private void initializePieceMaps() {
        //Credit: https://www.chessprogramming.org/Simplified_Evaluation_Function
        
        int[][] queens = {{-20,-10,-10, -5, -5,-10,-10,-20},
                          {-10,  0,  0,  0,  0,  0,  0,-10},
                          {-10,  0,  5,  5,  5,  5,  0,-10},
                          {-5,  0,  5,  5,  5,  5,  0, -5},
                          { 0,  0,  5,  5,  5,  5,  0, -5},
                          {-10,  5,  5,  5,  5,  5,  0,-10},
                          {-10,  0,  5,  0,  0,  0,  0,-10},
                          {-20,-10,-10, -5, -5,-10,-10,-20}};
        int[][] rooks = {{0,  0,  0,  0,  0,  0,  0,  0},
                         {5, 10, 10, 10, 10, 10, 10,  5},
                         {-5,  0,  0,  0,  0,  0,  0, -5},
                         { -5,  0,  0,  0,  0,  0,  0, -5},
                         {-5,  0,  0,  0,  0,  0,  0, -5},
                         {-5,  0,  0,  0,  0,  0,  0, -5},
                         {-5,  0,  0,  0,  0,  0,  0, -5},
                         {0,  0,  0,  5,  5,  0,  0,  0}};
        int[][] knights = {{-50,-40,-30,-30,-30,-30,-40,-50},
                          {-40,-20,  0,  0,  0,  0,-20,-40},
                          {-30,  0, 10, 15, 15, 10,  0,-30},
                          {-30,  5, 15, 20, 20, 15,  5,-30},
                          {-30,  0, 15, 20, 20, 15,  0,-30},
                          {-30,  5, 10, 15, 15, 10,  5,-30},
                          {-40,-20,  0,  5,  5,  0,-20,-40},
                          {-50,-40,-30,-30,-30,-30,-40,-50}};
        int[][] bishops = {{-20,-10,-10,-10,-10,-10,-10,-20},
                          {-10,  0,  0,  0,  0,  0,  0,-10},
                          {-10,  0,  5, 10, 10,  5,  0,-10},
                          {-10,  5,  5, 10, 10,  5,  5,-10},
                          {-10,  0, 10, 10, 10, 10,  0,-10},
                          {-10, 10, 10, 10, 10, 10, 10,-10},
                          {-10,  5,  0,  0,  0,  0,  5,-10},
                          {-20,-10,-10,-10,-10,-10,-10,-20}};
        int[][] pawns = {{0,  0,  0,  0,  0,  0,  0,  0},
                         {50, 50, 50, 50, 50, 50, 50, 50},
                         {10, 10, 20, 30, 30, 20, 10, 10},
                         {5,  5, 10, 25, 25, 10,  5,  5},
                         {0,  0,  0, 20, 20,  0,  0,  0},
                         {5, -5,-10,  0,  0,-10, -5,  5},
                         {5, 10, 10,-20,-20, 10, 10,  5},
                         {0,  0,  0,  0,  0,  0,  0,  0}};
        queenMap = queens;
        rookMap = rooks;
        knightMap = knights;
        bishopMap = bishops;
        pawnMap = pawns;
    }
}
