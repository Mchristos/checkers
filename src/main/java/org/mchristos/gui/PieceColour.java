package org.mchristos.gui;

public enum PieceColour {
    RED,
    BLACK;

    public PieceColour getOpposite() {
        PieceColour result = null;
        if (this == RED) {
            result = BLACK;
        } else if (this == BLACK) {
            result = RED;
        }
        if (result == null) {
            throw new RuntimeException("Null piece has no opposite");
        }
        return result;
    }
}

