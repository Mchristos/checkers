package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Black or white checker piece (clickable button component)
 */
public class GhostButton extends JButton{
    private BoardState boardstate;

    public GhostButton(BoardState state, ImageIcon image){
        super(image);
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
    }

    public BoardState getBoardstate() {
        return boardstate;
    }
}
