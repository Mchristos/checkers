package main.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private final int memory = 5;
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
        // update state with AI move
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

    public ArrayList<BoardState> getValidMoves(Player player, int pos) {
        return state.peek().getSuccessors(player, pos);
    }

    public void undo(){
        if (state.size() > 2){
            state.pop();
            state.pop();
        }
    }

}
