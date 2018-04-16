package main;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Black or white checker piece (clickable button component)
 */
public class CheckerButton extends JButton{

    private int position;
    private Piece type;
    public final int WIDTH = 50;
    public final int HEIGHT = 50;

    public CheckerButton(int position, Piece type){
        super();
        this.position = position;
        this.type = type;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    public int getPosition() {
        return position;
    }

    public Piece getType() {
        return type;
    }

    private void setIcon(){
        BufferedImage buttonIcon = null;
        if (type == Piece.BLACK){
            try {
                buttonIcon = ImageIO.read(new File("images/blackchecker.png"));
            }
            catch (IOException e){
                System.out.println(e.toString());
            }
        }
        else if (type == Piece.WHITE){
            try {
                buttonIcon = ImageIO.read(new File("images/whitechecker.gif"));
            }
            catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        else{
            throw new ValueException("Invalid Piece enum (must be Black or White) ");
        }
        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(WIDTH, HEIGHT,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
