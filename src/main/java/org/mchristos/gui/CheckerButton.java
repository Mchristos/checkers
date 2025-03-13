package org.mchristos.gui;

import org.mchristos.game.Piece;
import org.mchristos.game.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Black or white checker piece (clickable button component)
 */
public class CheckerButton extends JButton {

    private final int position;
    private final Piece piece;
    private static final Border YELLOW_BORDER = BorderFactory.createLineBorder(Color.YELLOW, 2);
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

    // drag drop
    int X;
    int Y;
    int screenX = 0;
    int screenY = 0;

    public CheckerButton(int position, Piece piece, GUI gui) {
        super();
        this.position = position;
        this.piece = piece;
        this.setBorder(EMPTY_BORDER);
        this.setContentAreaFilled(false);
        setCustomIcon(piece);
        if (piece.getPlayer() == Player.HUMAN) {
            if (SettingsPanel.dragDrop) {
                this.activateDragDrop(position, gui);
                return;
            }
            this.setFocusedBorder();
        }
    }

    /**
     * Toggle border highlight around piece onMouseFocus
     */
    private void setFocusedBorder() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(YELLOW_BORDER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(EMPTY_BORDER);
            }
        });
    }


    /**
     * (Only if drag-drop mode is ON) Compute mouse drag distance on the Board
     *
     * @param position position of piece on board
     * @param gui      the parent element (GUI)
     */
    private void activateDragDrop(int position, final GUI gui) {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                screenX = mouseEvent.getXOnScreen();
                screenY = mouseEvent.getYOnScreen();
                X = getX();
                Y = getY();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                int deltaX = mouseEvent.getXOnScreen() - screenX;
                int deltaY = mouseEvent.getYOnScreen() - screenY;
                int dx = (int) Math.round((double) deltaX / (double) SettingsPanel.squareSize);
                int dy = (int) Math.round((double) deltaY / (double) SettingsPanel.squareSize);
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


    public int getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    /**
     * Reads image from `resources` folder
     *
     * @param fileName name of file
     * @return InputStream of data
     */
    protected static URL getImageResource(final String fileName) {
        return CheckerButton.class.getClassLoader().getResource(fileName);
    }

    private void setCustomIcon(Piece piece) {
        BufferedImage buttonIcon = null;
        PieceColour pieceColour = SettingsPanel.getColour(piece.getPlayer());
        try {
            if (pieceColour == PieceColour.BLACK) {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(getImageResource("images/blackking.png"));
                } else {
                    buttonIcon = ImageIO.read(getImageResource("images/blackchecker.gif"));
                }
            } else {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(getImageResource("images/whiteking.png"));
                } else {
                    buttonIcon = ImageIO.read(getImageResource("images/whitechecker.gif"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Image files missing! Check 'resources' folder", e);
        }

        if (buttonIcon != null) {
            Image resized = buttonIcon.getScaledInstance(SettingsPanel.checkerWidth, SettingsPanel.checkerHeight, Image.SCALE_DEFAULT);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
