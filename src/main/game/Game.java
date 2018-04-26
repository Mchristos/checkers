package main.game;

import java.util.ArrayList;
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

    public MoveFeedback playerMove(int fromPos, int dx, int dy){
        int toPos = fromPos + dx + BoardState.SIDE_LENGTH*dy;
        if (toPos > getState().state.length){
            return MoveFeedback.NOT_ON_BOARD;
        }
        // check for forced jumped
        ArrayList<BoardState> jumpSuccessors = this.state.peek().getSuccessors(Player.HUMAN, true);
        boolean jumps = jumpSuccessors.size() > 0;
        if (jumps){
            for (BoardState succ : jumpSuccessors){
                if (succ.getFromPos() == fromPos && succ.getToPos() == toPos){
                    updateState(succ);
                    return MoveFeedback.SUCCESS;
                }
            }
            return MoveFeedback.FORCED_JUMP;
        }
        // check diagonal
        if (Math.abs(dx) != Math.abs(dy)){
            return MoveFeedback.NOT_DIAGONAL;
        }
        // check for move onto piece
        if (this.getState().state[toPos] != null){
            return MoveFeedback.NO_FREE_SPACE;
        }
        // check for non-jump moves
        ArrayList<BoardState> nonJumpSuccessors = this.state.peek().getSuccessors(Player.HUMAN, fromPos, false);
        for (BoardState succ : nonJumpSuccessors){
            if (succ.getFromPos() == fromPos && succ.getToPos() == toPos){
                updateState(succ);
                return MoveFeedback.SUCCESS;
            }
        }
        // TODO work out what went wrong
        if (dy > 1){
            return MoveFeedback.NO_BACKWARD_MOVES_FOR_SINGLES;
        }
        if (Math.abs(dx)== 2){
            return MoveFeedback.ONLY_SINGLE_DIAGONALS;
        }
        return MoveFeedback.UNKNOWN_INVALID;

    }

    public void aiMove(){
        // update state with AI move
        if (!isGameOver() && state.peek().getTurn() == Player.AI){
            BoardState newState = ai.move(this.state.peek(), Player.AI);
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
