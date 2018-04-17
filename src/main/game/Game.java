package main.game;

import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private final int memory = 5;
    private int player; // 0 == AI, 1 == human
    private boolean done;

    public Game(){
        state = new Stack<>();
        state.push(BoardState.InitialState());
//        done = false;
//        player = Settings.startingPlayer;
//        while(!done){
//            if(player == 0){
//
//            }
//            if(player == 1){
//
//            }
//        }
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
