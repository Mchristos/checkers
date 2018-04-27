package main.gui;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import main.game.Game;
import main.game.Piece;
import main.game.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Black or white checker piece (clickable button component)
 */
public class CheckerButton extends JButton{

    private int position;
    private Piece piece;
    private Game game;
    // drag drop
    int X;
    int Y;
    int screenX = 0;
    int screenY = 0;

    public CheckerButton(int position, Piece piece, GUI gui){
        super();
        this.position = position;
        this.piece = piece;
        this.game = game;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(piece);
        if (piece.getPlayer() == Player.HUMAN && Settings.dragDrop){
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    screenX = mouseEvent.getXOnScreen();
                    screenY = mouseEvent.getYOnScreen();
                    X = getX();
                    Y = getY();
                }
                @Override
                public void mouseReleased(MouseEvent mouseEvent){
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    int dx = (int) Math.round((double)deltaX / (double) main.gui.Settings.squareSize);
                    int dy = (int) Math.round((double)deltaY / (double) main.gui.Settings.squareSize);
                    gui.onMouseRelease(position, dx, dy);
                }
            });
            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent mouseEvent) {
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    setLocation(X + deltaX, Y + deltaY);
                }
            });
        }
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
                    buttonIcon = ImageIO.read(new File("images/blackchecker.gif"));
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
