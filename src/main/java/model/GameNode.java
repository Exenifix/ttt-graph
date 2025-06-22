package model;

import java.util.ArrayList;
import java.util.List;

public class GameNode {
    private final GameState state;
    private final List<GameNode> children = new ArrayList<>();
    private double winProbability;

    public GameNode(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public List<GameNode> getChildren() {
        return children;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(double winProbability) {
        this.winProbability = winProbability;
    }

    @Override
    public String toString() {
        return "Node{" +
                "prob=" + winProbability +
                ", children=" + children.size() +
                '}';
    }
}
