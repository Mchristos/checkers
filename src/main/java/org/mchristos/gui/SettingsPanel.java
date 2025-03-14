package org.mchristos.gui;

import org.mchristos.game.Player;

public class SettingsPanel {

    public static PieceColour AIcolour = PieceColour.BLACK; // Note: starting player gets RED pieces
    public static boolean helpMode = true;
    public static boolean hintMode = false;
    public static boolean dragDrop = false;
    public static final int AiMinPauseDurationInMs = 800;
    public static final int squareSize = 80;
    public static final int checkerWidth = 5 * squareSize / 6;
    public static final int checkerHeight = 5 * squareSize / 6;
    public static final int ghostButtonWidth = 30 * squareSize / 29;
    public static final int ghostButtonHeight = 5 * squareSize / 6;

    /**
     * Gets the correct colour (black/white) for the given player
     *
     * @param player current player
     * @return the colour
     */
    public static PieceColour getColour(Player player) {
        PieceColour result = null;
        if (player == Player.AI) {
            result = AIcolour;
        } else if (player == Player.HUMAN) {
            result = AIcolour.getOpposite();
        }
        if (result == null) {
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}
