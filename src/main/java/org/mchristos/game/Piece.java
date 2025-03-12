package org.mchristos.game;

public class Piece {

    private final Player player;
    private final boolean king;

    public Piece(Player player, boolean king) {
        this.player = player;
        this.king = king;
    }

    public boolean isKing() {
        return king;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Get possible y-direction movements
     *
     * @return array of y movements
     */
    public int[] getYMovements() {
        int[] result = new int[]{};
        if (king) {
            result = new int[]{-1, 1};
        } else {
            result = switch (player) {
                case AI -> new int[]{1};
                case HUMAN -> new int[]{-1};
            };
        }
        return result;
    }

    /**
     * Get possible x-direction movements
     *
     * @return array of x movements
     */
    public int[] getXMovements() {
        return new int[]{-1, 1};
    }

}
