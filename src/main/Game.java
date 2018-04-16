package main;

public class Game {
    private BoardState state;

    public Game(){
        state = BoardState.InitialState();
    }

    public void updateState(BoardState newState){
        state = newState;
    }

    public BoardState getState() {
        return state;
    }
}
