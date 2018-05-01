package main.game;

import java.util.ArrayList;
import java.util.Random;

public class AI {

    // determines the depth that the AI searches to in minimax
    private int depth;
    // which player the AI searches with respect to
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
        if (state.getTurn() == player){
            ArrayList<BoardState> successors = state.getSuccessors();
            return minimaxMove(successors);
        }
        else{
            throw new RuntimeException("Cannot generate moves for player if it's not their turn");
        }
    }

    /**
     * Chooses best successor state based on the minimax algorithm.
     * @param successors
     * @return
     */
    private BoardState minimaxMove(ArrayList<BoardState> successors){
        if (successors.size() == 1){
            return successors.get(0);
        }
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
        // choose randomly from equally scoring best moves
        return randomMove(equalBests);
    }

    /**
     * Chooses a successor state randomly.
     * @param successors
     * @return
     */
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
        // initialize alpha (computed as a max)
        int alpha = Integer.MIN_VALUE;
        // initialize beta (computed as a min)
        int beta = Integer.MAX_VALUE;
        // call minimax
        return minimax(node, depth, alpha, beta);
    }

    /**
     * Implements the minimax algorithm with alpha-beta pruning
     * @param node
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private int minimax(BoardState node, int depth, int alpha, int beta){
        if (depth == 0 || node.isGameOver()){
            return node.computeHeuristic(this.player);
        }
        // MAX player = player
        if (node.getTurn() == player){
            // player tries to maximize this value
            int v = Integer.MIN_VALUE;
            for (BoardState child : node.getSuccessors()){
                v = Math.max(v, minimax(child, depth-1, alpha, beta));
                alpha = Math.max(alpha, v);
                // prune
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        // MIN player = opponent
        if (node.getTurn() == player.getOpposite()){
            // opponent tries to minimize this value
            int v = Integer.MAX_VALUE;
            for (BoardState child : node.getSuccessors()){
                v = Math.min(v,minimax(child, depth-1, alpha, beta));
                beta = Math.min(beta, v);
                // prune
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        throw new RuntimeException("Error in minimax algorithm");
    }
}
