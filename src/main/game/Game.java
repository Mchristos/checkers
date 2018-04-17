package main.game;

import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private final int memory = 5;
    private int player; // 0 == AI, 1 == human
    private AI ai;

    public Game(){
        state = new Stack<>();
        state.push(BoardState.InitialState());
        ai = new AI();
    }

    public void playerMove(BoardState newState){
        updateState(newState);
    }

    public void aiMove(){
        BoardState newState = ai.move(this.state.peek());
        updateState(newState);
    }

    private void updateState(BoardState newState){
        state.push(newState);
        if(state.size() > memory){
            state.remove(0);
        }
    }

    public BoardState getState() {
        return state.peek();
    }

    public void undo(){
        if (state.size() > 1){
            state.pop();
        }
    }

}
