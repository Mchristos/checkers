package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Black or white square panel on checkerboard
 */
public class SquarePanel extends JPanel {

    public final int WIDTH = 60;
    public final int HEIGHT = 60;
    private Color color;

    public SquarePanel(int i, int j){
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        if( ((i % 2) + (j % 2)) % 2 == 0){
            color = Color.WHITE;
        }
        else{
            color = Color.BLACK;
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}
