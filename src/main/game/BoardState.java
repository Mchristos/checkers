package main.game;

import java.util.ArrayList;

public class BoardState {

    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH; // 8 x 8
    Player[] state;
    // stores the destination position of the most recent move to get to this state.
    private int fromPos;
    private int toPos;
    private boolean jumped;

    public BoardState(){
        state = new Player[BoardState.NO_SQUARES];
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
                    bs.state[i] = Player.AI;
                }
                // Human pieces in last 3 rows
                else if (y > 4){
                    bs.state[i] = Player.HUMAN;
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
            if(state[i] == player){
                result.addAll(getSuccessors(player, i, jump));
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
        if (this.getPlayer(position) != player){
            throw new IllegalArgumentException("No such piece at that position");
        }
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        int[] dxs = new int[]{-1, 1};
        int dy = -100000;
        switch (player){
            case AI:
                dy = 1;
                break;
            case HUMAN:
                dy = -1;
                break;
        }
        if(jump){
            return jumpSuccessors(player, position, dxs, dy);
        }
        else{
            return nonJumpSuccessors(player, position, dxs, dy);
        }
    }

    private ArrayList<BoardState> nonJumpSuccessors(Player player, int position, int[] dxs, int dy){
        ArrayList<BoardState> result = new ArrayList<>();
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        for (int dx : dxs){
            int newX = x + dx;
            int newY = y + dy;
            // new position valid?
            if (isValid(newY, newX)) {
                // new position available?
                if (getPlayer(newY, newX) == null) {
                    int newpos = SIDE_LENGTH*newY + newX;
                    BoardState newState = this.deepCopy();
                    // move piece
                    newState.state[position] = null;
                    newState.state[newpos] = player;
                    // store meta data
                    newState.fromPos = position;
                    newState.toPos = newpos;
                    newState.jumped = false;
                    result.add(newState);
                }
            }
        }
        return result;
    }


    private ArrayList<BoardState> jumpSuccessors(Player player, int position, int[] dxs, int dy){
        ArrayList<BoardState> result = new ArrayList<>();
        int y = position / SIDE_LENGTH;
        int x = position % SIDE_LENGTH;
        for (int dx : dxs){
            int newX = x + dx;
            int newY = y + dy;
            // new position valid?
            if (isValid(newY, newX)) {
                // new position contain opposite player?
                if (getPlayer(newY, newX) == player.getOpposite() ) {
                    newX = newX + dx; newY = newY + dy;
                    // jump position valid?
                    if (isValid(newY, newX)){
                        // jump position available?
                        if (getPlayer(newY,newX) == null) {
                            int newpos = SIDE_LENGTH*newY + newX;
                            BoardState newState = this.deepCopy();
                            // move piece
                            newState.state[position] = null;
                            newState.state[newpos] = player;
                            // store meta data
                            newState.fromPos = position;
                            newState.toPos = newpos;
                            newState.jumped = true;
                            // remove captured piece
                            newState.state[newpos - SIDE_LENGTH*dy - dx] = null;
                            result.add(newState);
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
    public Player getPlayer(int i){
        return state[i];
    }

    /**
     * Get piece by grid position
     * @param y
     * @param x
     * @return
     */
    public Player getPlayer(int y, int x){
        return getPlayer(SIDE_LENGTH*y + x);
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
