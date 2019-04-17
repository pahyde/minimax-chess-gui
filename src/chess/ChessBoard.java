package chess;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ChessBoard extends JPanel {
    
    JButton[][] cells = new JButton[8][8];
    boolean isUserMove = true;
    
    int x,y;
    boolean pieceSelected = false;
    
    ChessEngine engine = new ChessEngine();
   
    int WHITE = 0, BLACK = 1;
    int KING = 0, QUEEN = 1, BISHOP = 2, KNIGHT = 3, ROOK = 4, PAWN = 5;
    BufferedImage[][] pieces = new BufferedImage[2][6];
    
    public ChessBoard() {
        setLayout(new GridLayout(8,8));
        
        //File imageFile = new File("rec/images/pieces.png");
        BufferedImage image = null;
        try {
            System.out.println(this.getClass().getResource("/icon/re.png"));
            BufferedImage bi = ImageIO.read(this.getClass().getResource("/icon/re.png"));
            image = bi;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                
                Insets buttonMargin = new Insets(0, 0, 0, 0);
                cells[i][j] = new JButton();
                cells[i][j].setOpaque(true);
                cells[i][j].setBorderPainted(false);
                cells[i][j].setBackground(isBrownSquare(i,j) ? new Color(120, 80, 20) : Color.BLACK);
                
                cells[i][j].setMargin(buttonMargin);
                
                cellClickListener cl = new cellClickListener();
                cells[i][j].addActionListener(cl);
                
                add(cells[i][j]);
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                int x = image.getWidth() / 6 * j;
                int y = i * (image.getHeight() / 2 - 5);
                int w = image.getWidth() / 6;
                int h = image.getHeight() / 2;
                pieces[i][i == 0 ? 5 - j : j] = image.getSubimage(x,y,w,h);
            }
        }
        
        int[] w1 = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
        //int[] b1 = {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK};
        for (int i = 0; i < 8; i++) cells[7][i].setIcon(new ImageIcon(pieces[0][w1[i]]));
        for (int i = 0; i < 8; i++) cells[0][i].setIcon(new ImageIcon(pieces[1][w1[i]]));
        for (int i = 0; i < 8; i++) {
            cells[6][i].setIcon(new ImageIcon(pieces[WHITE][PAWN]));
            cells[1][i].setIcon(new ImageIcon(pieces[BLACK][PAWN]));
        }
    }
    
    private boolean isBrownSquare(int i, int j) {
        return ((i % 2) + (j % 2)) % 2 == 0;
    }
    
    private void handleUserMove(int y0, int x0, int y1, int x1) {
        System.out.printf("%d, %d, %d, %d\n",y0,x0,y1,x1);
        //input move to board representation
        //if valid move, update board rep, update view
        if (engine.userMove(x0, y0, x1, y1)) {
            cells[y1][x1].setIcon(cells[y0][x0].getIcon());
            cells[y0][x0].setIcon(null);
            
            int[][] move = engine.agentMove();
            int y00 = move[0][0], x00 = move[0][1], y11 = move[1][0], x11 = move[1][1];
            System.out.printf("%d %d %d %d\n", y00,x00,y11,x11);
            cells[y11][x11].setIcon(cells[y00][x00].getIcon());
            cells[y00][x00].setIcon(null);
        }
        pieceSelected = false;
    }
    
    private class cellClickListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (isUserMove) {
                for (int i = 0; i < cells.length; i++) {
                    for (int j = 0; j < cells[0].length; j++) {
                        if (e.getSource() == cells[i][j]) {
                            
                            if (pieceSelected) {
                                handleUserMove(y,x,i,j);
                            } else {
                                y = i;
                                x = j;
                                pieceSelected = true;
                            }
                                
                            //checkifGameOver()
                            
                            //AgentMove()
                        }
                    }
                }
            }
        }
        
    }
}
