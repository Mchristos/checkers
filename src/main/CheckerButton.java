package main;

import javax.swing.*;
/**
 * Black or white checker piece (clickable button component)
 */
public class CheckerButton extends JButton{
    private int position;
    private Piece type;

    public CheckerButton(int position,Piece type, ImageIcon image){
        super(image);
        this.position = position;
        this.type = type;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
    }

    public int getPosition() {
        return position;
    }

    public Piece getType() {
        return type;
    }
}
