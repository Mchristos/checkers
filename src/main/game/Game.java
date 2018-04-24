package main.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private int memory;
    private AI ai;

    public Game(){
        memory = Settings.UNDO_MEMORY;
        state = new Stack<>();
        state.push(BoardState.InitialState());
        ai = new AI();
    }

    public void playerMove(BoardState newState){
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN){
            updateState(newState);
        }
    }

    public void aiMove(){
        // update state with AI move
        if (!isGameOver() && state.peek().getTurn() == Player.AI){
            BoardState newState = ai.move(this.state.peek());
            updateState(newState);
        }
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

    public Player getTurn() {
        return state.peek().getTurn();
    }

    public boolean isGameOver(){
        return state.peek().isGameOver();
    }

    public void undo(){
        if (state.size() > 2){
            state.pop();
            while(state.peek().getTurn() == Player.AI){
                state.pop();
            }
        }
    }

}
