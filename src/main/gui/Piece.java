package main.gui;

public enum Piece {
    WHITE,
    BLACK;

    public Piece getOpposite(){
        Piece result = null;
        if (this == WHITE){
            result = BLACK;
        }
        else if (this == BLACK){
            result = WHITE;
        }
        if(result == null){
            throw new RuntimeException("Null piece has no opposite");
        }
        return result;
    }
}

