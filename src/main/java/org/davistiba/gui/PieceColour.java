package org.davistiba.gui;

public enum PieceColour {
    WHITE,
    BLACK;

    public PieceColour getOpposite() {
        PieceColour result = null;
        if (this == WHITE) {
            result = BLACK;
        } else if (this == BLACK) {
            result = WHITE;
        }
        if (result == null) {
            throw new RuntimeException("Null piece has no opposite");
        }
        return result;
    }
}

