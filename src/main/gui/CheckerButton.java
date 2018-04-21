package main.gui;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import main.game.Piece;
import main.game.Player;

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
    private Piece piece;

    public CheckerButton(int position, Piece piece){
        super();
        this.position = position;
        this.piece = piece;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(piece);
    }

    public int getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    private void setIcon(Piece piece){
        BufferedImage buttonIcon = null;
        Colour colour = Settings.getColour(piece.getPlayer());
        try {
            if (colour == Colour.BLACK) {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/blackking.png"));
                } else {
                    buttonIcon = ImageIO.read(new File("images/blackchecker.png"));
                }
            }
            else {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/whiteking.png"));
                }
                else {
                    buttonIcon = ImageIO.read(new File("images/whitechecker.gif"));
                }
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
        }

        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.checkerWidth,Settings.checkerHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
