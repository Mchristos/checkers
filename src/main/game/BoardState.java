package main.game;

import java.util.ArrayList;

public class BoardState {

    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH; // 8 x 8
    Piece[] state;
    // stores the destination position of the most recent move to get to this state.
    private int fromPos;
    private int toPos;
    private boolean jumped;

    public BoardState(){
        state = new Piece[BoardState.NO_SQUARES];
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
        return bs;
    }

    private BoardState deepCopy(){
        BoardState bs = new BoardState();
        for (int i = 0; i < bs.state.length; i++){
            bs.state[i] = this.state[i];
        }
        return bs;
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
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        int[] dxs = new int[]{-1, 1};
        int[] dys = new int[]{};
        Piece piece = this.state[position];
        if (piece.isKing()){
            dys = new int[]{-1,1};
        }
        else{
            switch (piece.getPlayer()){
                case AI:
                    dys = new int[]{1};
                    break;
                case HUMAN:
                    dys = new int[]{-1};
                    break;
            }
        }
        if(jump){
            return jumpSuccessors(piece, position, dxs, dys);
        }
        else{
            return nonJumpSuccessors(piece, position, dxs, dys);
        }
    }

    private ArrayList<BoardState> nonJumpSuccessors(Piece piece, int position, int[] dxs, int[] dys){
        ArrayList<BoardState> result = new ArrayList<>();
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        for (int dx : dxs){
            for (int dy : dys){
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

    private BoardState createNewState(int oldPos, int newPos, Piece piece, boolean jumped, int dy, int dx){
        // check if king position
        if (isKingPosition(newPos, piece.getPlayer())){
            piece = new Piece(piece.getPlayer(), true);
        }
        BoardState newState = this.deepCopy();
        // move piece
        newState.state[oldPos] = null;
        newState.state[newPos] = piece;
        // store meta data
        newState.fromPos = oldPos;
        newState.toPos = newPos;
        newState.jumped = jumped;
        if (jumped){
            // remove captured piece
            newState.state[newPos - SIDE_LENGTH*dy - dx] = null;
        }
        return newState;
    }

    private boolean isKingPosition(int pos, Player player){
        int y = pos / SIDE_LENGTH;
        if (y == 0 && player == Player.HUMAN){
            return true;
        }
        else if (y == SIDE_LENGTH-1 && player == Player.AI){
            return true;
        }
        else{
            return false;
        }
    }


    private ArrayList<BoardState> jumpSuccessors(Piece piece, int position, int[] dxs, int[] dys){
        ArrayList<BoardState> result = new ArrayList<>();
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        for (int dx : dxs){
            for (int dy : dys){
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position contain opposite player?
                    if (getPiece(newY,newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite() ) {
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

    /**
     * Gets the destination position of the most recent move to get to this state.
     * @return
     */
    public int getToPos(){
        return this.toPos;
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
    public Piece getPiece(int y, int x){
        return getPiece(SIDE_LENGTH*y + x);
    }

    /**
     * Check if grid indices are valid
     * @param y
     * @param x
     * @return
     */
    public boolean isValid(int y, int x){
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}
