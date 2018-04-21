package main.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import main.game.BoardState;

/**
 * Button representing a possible move for a player
 */
public class GhostButton extends JButton{

    private BoardState boardstate;

    public GhostButton(BoardState state){
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    private void setIcon(){
        BufferedImage buttonIcon = null;
        try{
            buttonIcon = ImageIO.read(new File("images/dottedcircle2.png"));
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.ghostButtonWidth,Settings.ghostButtonHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }


    public BoardState getBoardstate() {
        return boardstate;
    }
}
