package org.davistiba.gui;

import org.davistiba.game.BoardState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Button representing a possible move for a player
 */
public class GhostButton extends JButton {

    private final BoardState boardstate;

    public GhostButton(BoardState state) {
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    private static URL getImageResource(final String fileName) {
        return GhostButton.class.getClassLoader().getResource(fileName);
    }

    private void setIcon() {
        BufferedImage buttonIcon = null;
        try {
            if (SettingsPanel.helpMode) {
                buttonIcon = ImageIO.read(getImageResource("images/dottedcircle.png"));
            } else {
                buttonIcon = ImageIO.read(getImageResource("images/dottedcircleyellow.png"));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (buttonIcon != null) {
            Image resized = buttonIcon.getScaledInstance(SettingsPanel.ghostButtonWidth, SettingsPanel.ghostButtonHeight, Image.SCALE_DEFAULT);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }


    public BoardState getBoardstate() {
        return boardstate;
    }
}
