package chess;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

public class ChessGUI extends JFrame {
    public static void main(String[] args) {
        new ChessGUI();
    }
    
    public ChessGUI() {
        
        setSize(820,820);
        setLocationRelativeTo(null);   
        
        add(new ChessBoard(), BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CHESS");
        setVisible(true);
    }
}
