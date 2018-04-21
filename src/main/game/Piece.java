package main.game;

public class Piece {

    private Player player;
    private boolean king;

    public Piece(Player player, boolean king){
        this.player = player;
        this.king = king;
    }

    public boolean isKing() {
        return king;
    }

    public Player getPlayer() {
        return player;
    }
}
