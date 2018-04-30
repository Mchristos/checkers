package main.game;

import java.util.ArrayList;
import java.util.Random;

public class AI {

    private int depth;
    private Player player;

    public AI(){
        depth = Settings.AI_DEPTH;
        player = Player.AI;
    }

    public AI(int depth, Player player){
        this.depth = depth;
        this.player = player;
    }

    public BoardState move(BoardState state, Player player){
        if (state.getTurn()==player){
            ArrayList<BoardState> successors = state.getSuccessors();
            return minimaxMove(successors);
        }
        else{
            throw new RuntimeException("Invalid player");
        }
    }

    private BoardState minimaxMove(ArrayList<BoardState> successors){
        int bestScore = Integer.MIN_VALUE;
        ArrayList<BoardState> equalBests = new ArrayList<>();
        for (BoardState succ : successors){
            int val = minimax(succ, this.depth);
            if (val > bestScore){
                bestScore = val;
                equalBests.clear();
            }
            if (val == bestScore){
                equalBests.add(succ);
            }
        }
        if(equalBests.size() > 1){
            System.out.println(player.toString() + " choosing random best move");
        }
        return randomMove(equalBests);

    }
    private BoardState randomMove(ArrayList<BoardState> successors){
        if (successors.size() < 1){
            throw new RuntimeException("Can't randomly choose from empty list.");
        }
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
//            if(node.pieceCount.get(player.getOpposite()) == 0 ) {
//                System.out.println(player.toString() + " can see a winning sequence");
//            }
            return node.computeHeuristic(this.player);
        }
        if (node.getTurn() == player){ // MAX
            // AI tries to maximize this value
            int v = Integer.MIN_VALUE;
            for (BoardState child : node.getSuccessors()){
                v = Math.max(v, minimax(child, depth-1, alpha, beta));
                alpha = Math.max(alpha, v);
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        if (node.getTurn() == player.getOpposite()){ // MIN
            // human tries to minimize this value
            int v = Integer.MAX_VALUE;
            for (BoardState child : node.getSuccessors()){
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
