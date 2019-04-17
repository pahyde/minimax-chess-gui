package chess;

import java.util.*;

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
        int maxWinProb = -1000;
        
        //REFACTOR to declarative compositional style
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (blackPieceVals.containsKey(board[i][j])) {
                    for (int[] potentialMove : potentialMoves(i,j,board[i][j])) {
                        int i1 = potentialMove[0], j1 = potentialMove[1];
                        if (j1 >= 0 && j1 < 8 && i1 >= 0 && i1 < 8 && isValidMove(j,i,j1,i1,board[i][j])) {
                            //System.out.println("piece: " + board[i][j] + " i1: " + i1 + " j1: " + j1);
                            int winProb = miniMax(i,j,i1,j1,board[i][j],0);
                            if (winProb > maxWinProb) {
                                maxWinProb = winProb;
                                int[][] localMax = {{i,j}, {i1,j1}};
                                move = localMax;
                            }
                        }
                    }
                }
            }
        }
        
        int y1 = move[1][0], x1 = move[1][1];
        int y0 = move[0][0], x0 = move[0][1];
        board[y1][x1] = board[y0][x0];
        board[y0][x0] = null;
        //evaluate move
        
        return move;
    }

    private List<int[]> potentialMoves(int i, int j, String s) {
        char p = s.charAt(1);
        if (p == 'Q') return queenPotentialMoves(i,j,p);
        if (p == 'R') return rookPotentialMoves(i,j,p);
        if (p == 'K') return knightPotentialMoves(i,j,p);
        if (p == 'B') return bishopPotentialMoves(i,j,p);
        if (p == 'P') return pawnPotentialMoves(i,j,s);
        else          return kingPotentialMoves(i,j,p);
    }

    private List<int[]> queenPotentialMoves(int y, int x, char p) {
        List<int[]> rook = rookPotentialMoves(y,x,p);
        List<int[]> bish = bishopPotentialMoves(y,x,p);
        rook.addAll(bish);
        return rook;
    }

    private List<int[]> kingPotentialMoves(int y, int x, char p) {
        List<int[]> res = new ArrayList();
        for (int i = -1; i <= 1; i++)  {
            for (int j = -1; j <= 1; j++) {
                int[] move = {y+i,x+j};
                res.add(move);
            }
        }
        return res;
    }

    private List<int[]> pawnPotentialMoves(int y, int x, String s) {
        List<int[]> res = new ArrayList();
        for (int i = -1; i <= 1; i++) {
            res.add(new int[] {s.charAt(0) == 'W' ? y-1 : y+1,i});
        }
        return res;
    }

    private List<int[]> bishopPotentialMoves(int y, int x, char p) {
        List<int[]> res = new ArrayList();
        for (int d = 1; y + d < 8  && x + d < 8 ; d++) res.add(new int[] {y+d,x+d});
        for (int d = 1; y - d >= 0 && x + d < 8 ; d++) res.add(new int[] {y-d,x+d});
        for (int d = 1; y + d < 8  && x - d >= 0; d++) res.add(new int[] {y+d,x-d});
        for (int d = 1; y - d >= 0 && x - d >= 0; d++) res.add(new int[] {y-d,x-d});
        return res;
    }

    private List<int[]> knightPotentialMoves(int y, int x, char p) {
        List<int[]> res = new ArrayList();
        int[][] moves = {{y+2,x+1},{y+2,x-1},{y+1,x+2},{y+1,x-2},
                         {y-2,x+1},{y-2,x-1},{y-1,x+2},{y-1,x-2}};
        for (int[] move : moves) res.add(move);
        return res;
    }

    private List<int[]> rookPotentialMoves(int y, int x, char p) {
        List<int[]> res = new ArrayList();
        for (int i = x + 1; i < 8; i++) res.add(new int[] {y,i});
        for (int i = y + 1; i < 8; i++) res.add(new int[] {i,x});
        for (int i = x - 1; i >= 0; i--) res.add(new int[] {y,i});
        for (int i = y - 1; i >= 0; i--) res.add(new int[] {i,x});
        return res;
    }

    
    public int valueBoardState() {
        int pieceDiff = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if (piece != null) {
                    char color = piece.charAt(0), rank = piece.charAt(1);
                    if (rank == 'Q') pieceDiff += 9 * (color == 'B' ? 1 : -1);
                    if (rank == 'R') pieceDiff += 5 * (color == 'B' ? 1 : -1);
                    if (rank == 'K') pieceDiff += 3 * (color == 'B' ? 1 : -1);
                    if (rank == 'B') pieceDiff += 3 * (color == 'B' ? 1 : -1);
                    if (rank == 'P') pieceDiff += 1 * (color == 'B' ? 1 : -1);
                }
            }
        }
        return pieceDiff;
        
    }
    
    
    //
    public int miniMax(int y0, int x0, int y1, int x1, String piece, int depth) {
        
        String piece2 = board[y1][x1];
        
        board[y1][x1] = piece;
        board[y0][x0] = null;
        
        if (depth == 2) {
            int value = valueBoardState();
            board[y1][x1] = piece2;
            board[y0][x0] = piece;
            return value;
        }
        
        Map<String,Integer> pieceVals = depth % 2 == 0 ? whitePieceVals : blackPieceVals;
        
        List<Integer> stateTree = new ArrayList();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieceVals.containsKey(board[i][j])) {
                    for (int[] potentialMove : potentialMoves(i,j,board[i][j])) {
                        int i1 = potentialMove[0], j1 = potentialMove[1];
                        if (j1 >= 0 && j1 < 8 && i1 >= 0 && i1 < 8 && isValidMove(j,i,j1,i1,board[i][j])) {
                            stateTree.add(miniMax(i,j,i1,j1,board[i][j],depth+1));
                        } 
                    }
                }
            }
        }
        int optimal;
        if (depth % 2 == 0) {
            optimal = 10000;
            for (int heur : stateTree) optimal = Math.min(optimal, heur);
        } else {
            optimal = -10000;
            for (int heur : stateTree) optimal = Math.max(optimal, heur);
        }
        
        board[y1][x1] = piece2;
        board[y0][x0] = piece;
        
        return optimal;
    }
    
    private void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    
    
    private boolean isValidMove(int x0, int y0, int x1, int y1, String piece) {
        char p = piece.charAt(1);
        if (p == 'Q') return validRook(x0,y0,x1,y1) || validBishop(x0,y0,x1,y1);
        if (p == 'R') return validRook(x0,y0,x1,y1);
        if (p == 'K') return validKnight(x0,y0,x1,y1);
        if (p == 'B') return validBishop(x0,y0,x1,y1);
        if (p == 'P') return validPawn(x0,y0,x1,y1, piece.charAt(0));
        if (p == 'k') return validKing(x0,y0,x1,y1);
        return false;
    }
    
    private boolean validKing(int x0, int y0, int x1, int y1) {
        int dy = Math.abs(y1 - y0), dx = Math.abs(x1 - x0);
        return dy < 2 && dx < 2 && dy + dx > 0;
    }

    private boolean validPawn(int x0, int y0, int x1, int y1, char color) {
        int dy = y1 - y0, dx = x1 - x0;
        return color == 'W' ? dy == -1 && dx == 0 : dy == 1 && dx == 0 ;
    }

    private boolean validKnight(int x0, int y0, int x1, int y1) {
        int dy = Math.abs(y1 - y0), dx = Math.abs(x1 - x0);
        return (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
    }

    private boolean validBishop(int x0, int y0, int x1, int y1) {
        int dy = y1 - y0, dx = x1 - x0;
        if (Math.abs(dy) != Math.abs(dx)) return false;
        int step = dy / Math.abs(dy);
        for (int i = y0; i != (y1+step) && i != 8 && i != -1; i += step) {
            int j = x0 + Math.abs(i-y0) * (dx / Math.abs(dx));
            if (i != y0 && board[i][j] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean validRook(int x0, int y0, int x1, int y1) {
        int dy = y1 - y0, dx = x1 - x0;
        if (dy == 0) {
            int step = dx / Math.abs(dx);
            for (int i = x0; i != (x1+step) && i != 8; i += step) {
                if (i != x0 && board[y0][i] != null) return false;
            }
            return true;
        } else if (dx == 0) {
            int step = dy / Math.abs(dy);
            for (int i = y0; i != (y1+step) && i != 8; i += step) {
                if (i != y0 && board[i][x0] != null) return false;
            }
            return true;
        }
        return false;
    }

    private void initializeBoard() {
        String[] r1 = {"R","K","B","Q","k","B","K","R"};
        for (int i = 0; i < 8; i++) {
            board[0][i] = "B" + r1[i];
            board[1][i] = "BP";
            board[7][i] = "W" + r1[i];
            board[6][i] = "WP";
        }
    }
    private void initializePieceVals() {
        whitePieceVals.put("WQ",9); whitePieceVals.put("WR",5); whitePieceVals.put("WK",3);
        whitePieceVals.put("WB",3); whitePieceVals.put("WP",3); whitePieceVals.put("Wk",100);
        
        blackPieceVals.put("BQ",9); blackPieceVals.put("BR",5); blackPieceVals.put("BK",3);
        blackPieceVals.put("BB",3); blackPieceVals.put("BP",3); blackPieceVals.put("Bk",100);
        
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
