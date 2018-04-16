package main;

import java.util.Stack;

public class Game {

    private Stack<BoardState> state;
    private final int memory = 5;

    public Game(){
        state = new Stack<>();
        state.push(BoardState.InitialState());
    }

    public void updateState(BoardState newState){
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
