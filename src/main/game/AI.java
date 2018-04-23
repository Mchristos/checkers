package main.game;

import java.util.ArrayList;
import java.util.Random;

public class AI {

    private int depth = 6;

    public AI(){

    }

    public BoardState move(BoardState state){
        ArrayList<BoardState> successors = state.getSuccessors(Player.AI);
        int bestScore = Integer.MIN_VALUE;
        BoardState result = null;
        for (BoardState succ : successors){
            int val = minimax(succ, this.depth);
            if (val > bestScore){
                result = succ;
                bestScore = val;
            }
        }
        return result;
    }

    private BoardState randomNext(ArrayList<BoardState> successors){
        Random rand = new Random();
        int i = rand.nextInt(successors.size());
        return successors.get(i);
    }

    private int minimax(BoardState node, int depth){
        if (depth == 0 || node.isGameOver()){
            return node.computeHeuristic(Player.AI);
        }
        if (node.getTurn() == Player.AI){ // MAX
            int best = - Integer.MAX_VALUE;
            for (BoardState child : node.getSuccessors(Player.AI)){
                int val = minimax(child, depth-1);
                best = Math.max(best, val);
            }
            return best;
        }
        if (node.getTurn() == Player.HUMAN){ // MIN
            int best = Integer.MAX_VALUE;
            for (BoardState child : node.getSuccessors(Player.HUMAN)){
                int val = minimax(child, depth-1);
                best = Math.min(best,val);
            }
            return best;
        }
        throw new RuntimeException("Error in minimax algorithm");
    }

}
