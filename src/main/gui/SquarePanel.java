package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Black or white square panel on checkerboard
 */
public class SquarePanel extends JPanel {

    private Color color;

    public SquarePanel(int i, int j){
        this.setPreferredSize(new Dimension(Settings.squareSize,Settings.squareSize));
        if( ((i % 2) + (j % 2)) % 2 == 0){
            color = Color.WHITE;
        }
        else{
            color = Color.BLACK;
        }
    }

    public void setHighlighted(){
        color = Color.GRAY;
    }


    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}