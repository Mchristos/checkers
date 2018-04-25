package main.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BoardState {

    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH; // 8 x 8
    Piece[] state;
    // stores the destination position of the most recent move to get to this state.
    private int fromPos = -1;
    private int toPos = -1;
    private int doublejumpPos = -1;
    private Player turn;
    // track number of human/AI pieces on board
    private HashMap<Player, Integer> pieceCount;
    private HashMap<Player, Integer> kingCount;

    public BoardState(){
        state = new Piece[BoardState.NO_SQUARES];
        turn = Settings.FIRSTMOVE;
    }

    /**
     * Set up initial board state.
     */
    public static BoardState InitialState(){
        BoardState bs = new BoardState();
        for (int i = 0; i < bs.state.length; i++){
            int y = i/SIDE_LENGTH;
            int x = i % SIDE_LENGTH;
            // place on black squares only
            if ((x + y) % 2 == 1 ){
                // AI pieces in first 3 rows
                if (y < 3){
                    bs.state[i] = new Piece(Player.AI, false);
                }
                // Human pieces in last 3 rows
                else if (y > 4){
                    bs.state[i] = new Piece(Player.HUMAN, false);
                }
            }
        }
        // count initial pieces 
        int aiCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.AI).count();
        int humanCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.HUMAN).count();
        bs.pieceCount = new HashMap<>();
        bs.pieceCount.put(Player.AI, aiCount);
        bs.pieceCount.put(Player.HUMAN,humanCount);
        bs.kingCount = new HashMap<>();
        bs.kingCount.put(Player.AI, 0);
        bs.kingCount.put(Player.HUMAN, 0);
        return bs;
    }

    private BoardState deepCopy(){
        BoardState bs = new BoardState();
        System.arraycopy(this.state, 0, bs.state, 0, bs.state.length);
        return bs;
    }

    /**
     * Compute heuristic indicating how desirable this state is to a given player. Computed as number of pieces minus
     * number of opponent pieces, with king pieces counted double.
     * @param player
     * @return
     */
    public int computeHeuristic(Player player){
        return this.pieceCount.get(player) + this.kingCount.get(player) - this.pieceCount.get(player.getOpposite())
                - this.kingCount.get(player.getOpposite());
    }

    /**
     * Second heuristic function: calculates the number of options open to the player.
     * @param player
     * @return
     */
    public int computeHeuristic2(Player player){
        return this.getSuccessors(player).size();
    }

    /**
     * Gets valid successor states for a player
     * @param player
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player){
        // compute jump successors
        ArrayList<BoardState> successors = getSuccessors(player, true);
        if (Settings.FORCETAKES){
            if (successors.size() > 0){
                // return only jump successors if available (forced)
                return  successors;
            }
            else{
                // return non-jump successors (since no jumps available)
                return getSuccessors(player, false);
            }
        }
        else{
            // return jump and non-jump successors
            successors.addAll(getSuccessors(player, false));
            return successors;
        }
    }

    /**
     * Get valid jump or non-jump successor states for a player
     * @param player
     * @param jump
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player, boolean jump){
        ArrayList<BoardState> result = new ArrayList<>();
        for (int i = 0; i < this.state.length; i++){
            if (state[i] != null){
                if(state[i].getPlayer() == player){
                    result.addAll(getSuccessors(player, i, jump));
                }
            }
        }
        return result;
    }

    /**
     * Gets valid successor states for a specific piece on the board
     * @param player
     * @param position position of piece
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player, int position){
        if (Settings.FORCETAKES){
            // compute jump successors GLOBALLY
            ArrayList<BoardState> jumps = getSuccessors(player, true);
            if (jumps.size() > 0){
                // return only jump successors if available (forced)
                return getSuccessors(player, position, true);
            }
            else{
                // return non-jump successors (since no jumps available)
                return getSuccessors(player, position, false);
            }
        }
        else{
            // return jump and non-jump successors
            ArrayList<BoardState> result = new ArrayList<>();
            result.addAll(getSuccessors(player, position, true));
            result.addAll(getSuccessors(player, position, false));
            return result;
        }
    }

    /**
     * Get valid jump or non-jump successor states for a specific piece on the board.
     * @param player
     * @param position
     * @return
     */
    public ArrayList<BoardState> getSuccessors(Player player, int position, boolean jump){
        if (this.getPiece(position).getPlayer() != player){
            throw new IllegalArgumentException("No such piece at that position");
        }
        Piece piece = this.state[position];
        if(jump){
            return jumpSuccessors(piece, position);
        }
        else{
            return nonJumpSuccessors(piece, position);
        }
    }

    private ArrayList<BoardState> nonJumpSuccessors(Piece piece, int position){
        ArrayList<BoardState> result = new ArrayList<>();
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position available?
                    if (getPiece(newY, newX) == null) {
                        int newpos = SIDE_LENGTH*newY + newX;
                        result.add(createNewState(position, newpos, piece, false, dy,dx));
                    }
                }
            }
        }
        return result;
    }

    private ArrayList<BoardState> jumpSuccessors(Piece piece, int position){
        ArrayList<BoardState> result = new ArrayList<>();
        // no other jump moves are valid while doing double jump
        if (doublejumpPos > 0 && position != doublejumpPos){
            return result;
        }
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position contain opposite player?
                    if (getPiece(newY,newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite()){
                        newX = newX + dx; newY = newY + dy;
                        // jump position valid?
                        if (isValid(newY, newX)){
                            // jump position available?
                            if (getPiece(newY,newX) == null) {
                                int newpos = SIDE_LENGTH*newY + newX;
                                result.add(createNewState(position, newpos, piece, true, dy, dx));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private BoardState createNewState(int oldPos, int newPos, Piece piece, boolean jumped, int dy, int dx){
        BoardState result = this.deepCopy();
        result.pieceCount = new HashMap<>(pieceCount);
        result.kingCount = new HashMap<>(kingCount);
        // check if king position
        boolean kingConversion = false;
        if (isKingPosition(newPos, piece.getPlayer())){
            piece = new Piece(piece.getPlayer(), true);
            kingConversion = true;
            // increase king count
            result.kingCount.replace(piece.getPlayer(), result.kingCount.get(piece.getPlayer()) + 1);
        }
        // move piece
        result.state[oldPos] = null;
        result.state[newPos] = piece;
        // store meta data
        result.fromPos = oldPos;
        result.toPos = newPos;
        Player oppPlayer = piece.getPlayer().getOpposite();
        result.turn = oppPlayer;
        if (jumped){
            // remove captured piece
            result.state[newPos - SIDE_LENGTH*dy - dx] = null;
            result.pieceCount.replace(oppPlayer, result.pieceCount.get(oppPlayer) - 1);
            // is another jump available? (not allowed if just converted into king)
            if (result.jumpSuccessors(piece, newPos).size() > 0 && kingConversion == false){
                // don't swap turns
                result.turn = piece.getPlayer();
                // remember double jump position
                result.doublejumpPos = newPos;
            }
        }
        return result;
    }

    private boolean isKingPosition(int pos, Player player){
        int y = pos / SIDE_LENGTH;
        if (y == 0 && player == Player.HUMAN){
            return true;
        }
        else return y == SIDE_LENGTH - 1 && player == Player.AI;
    }

    /**
     * Gets the destination position of the most recent move to get to this state.
     * @return
     */
    public int getToPos(){
        return this.toPos;
    }

    /**
     * Gets the player whose turn it is
     * @return
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Is the board in a game over state
     * @return
     */
    public boolean isGameOver(){
        return (pieceCount.get(Player.AI) == 0 || pieceCount.get(Player.HUMAN) == 0);
    }

    /**
     * Get player piece at given position.
     * @param i Position in board.
     * @return
     */
    public Piece getPiece(int i){
        return state[i];
    }

    /**
     * Get piece by grid position
     * @param y
     * @param x
     * @return
     */
    private Piece getPiece(int y, int x){
        return getPiece(SIDE_LENGTH*y + x);
    }

    /**
     * Check if grid indices are valid
     * @param y
     * @param x
     * @return
     */
    private boolean isValid(int y, int x){
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}
