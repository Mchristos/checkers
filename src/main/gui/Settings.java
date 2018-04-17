package main.gui;

import main.game.Player;

public class Settings{
    public static Piece AIcolour = Piece.BLACK; // Note: starting player gets black pieces

    /**
     * Gets the correct piece type (black/white) for the given player
     * @param player
     * @return
     */
    public static Piece getPiece(Player player){
        Piece result = null;
        if (player == Player.AI){
            result = Settings.AIcolour;
        }
        else if (player == Player.HUMAN){
            result = Settings.AIcolour.getOpposite();
        }
        if(result == null){
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}
