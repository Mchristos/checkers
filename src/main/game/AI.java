package main.game;

import java.util.ArrayList;
import java.util.Random;

public class AI {
    public AI(){}

    public BoardState move(BoardState state){
        Random rand = new Random();
        ArrayList<BoardState> successors = state.getSuccessors(Player.AI);
        int i = rand.nextInt(successors.size());
        return successors.get(i);
    }
}
