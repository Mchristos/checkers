package main.game;

public enum MoveFeedback {
    NOT_DIAGONAL ("You can only move diagonally."),
    FORCED_JUMP ("You're forced to take."),
    NO_FREE_SPACE ("You can't move onto another piece."),
    ONLY_SINGLE_DIAGONALS ("You can only make single moves."),
    NO_BACKWARD_MOVES_FOR_SINGLES ("Only kings can move backwards!"),
    NOT_ON_BOARD(""),
    PIECE_BLOCKED ("This piece has no diagonal moves."),
    UNKNOWN_INVALID("Not a valid move."),
    SUCCESS ("Success");

    private final String name;

    MoveFeedback(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

}
