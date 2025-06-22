package model;

public enum Player {
    X, O, EMPTY;

    public Player opponent() {
        return this == X ? O : X;
    }
}
