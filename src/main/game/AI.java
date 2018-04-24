package main.game;

import java.util.ArrayList;
import java.util.Random;

public class AI {

    private int depth;

    public AI(){
        depth = Settings.AI_DEPTH;
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

    /**
     * Implements the minimax algorithm with alpha-beta pruning
     * @param node
     * @param depth
     * @return minimax score associated with node
     */
    private int minimax(BoardState node, int depth){
        int alpha = Integer.MIN_VALUE; // alpha computed as a MAX
        int beta = Integer.MAX_VALUE; // beta computes as a MIN
        return minimax(node, depth, alpha, beta);
    }

    private int minimax(BoardState node, int depth, int alpha, int beta){
        if (depth == 0 || node.isGameOver()){
            return node.computeHeuristic(Player.AI);
        }
        if (node.getTurn() == Player.AI){ // MAX
            // AI tries to maximize this value
            int v = Integer.MIN_VALUE;
            for (BoardState child : node.getSuccessors(Player.AI)){
                v = Math.max(v, minimax(child, depth-1, alpha, beta));
                alpha = Math.max(alpha, v);
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        if (node.getTurn() == Player.HUMAN){ // MIN
            // human tries to minimize this value
            int v = Integer.MAX_VALUE;
            for (BoardState child : node.getSuccessors(Player.HUMAN)){
                v = Math.min(v,minimax(child, depth-1, alpha, beta));
                beta = Math.min(beta, v);
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        throw new RuntimeException("Error in minimax algorithm");
    }
}
