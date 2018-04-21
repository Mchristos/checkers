package main.gui;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
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
    private Player player;

    public CheckerButton(int position, Player player){
        super();
        this.position = position;
        this.player = player;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(player);
    }

    public int getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    private void setIcon(Player player){
        BufferedImage buttonIcon = null;
        Piece type = Settings.getPiece(this.player);
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
            Image resized = buttonIcon.getScaledInstance(Settings.checkerWidth,Settings.checkerHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
