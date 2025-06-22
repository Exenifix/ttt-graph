package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final Player[][] board;
    private final Player currentPlayer;

    public GameState(Player[][] board, Player currentPlayer) {
        this.board = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, 3);
        }
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isTerminal() {
        return getWinner() != null || isDraw();
    }

    public Player getWinner() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != Player.EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                return board[i][0];
            if (board[0][i] != Player.EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i])
                return board[0][i];
        }
        // Check diagonals
        if (board[0][0] != Player.EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];
        if (board[0][2] != Player.EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];
        return null;
    }

    public boolean isDraw() {
        if (getWinner() != null) return false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Player.EMPTY) return false;
            }
        }
        return true;
    }

    public List<GameState> getNextStates() {
        List<GameState> states = new ArrayList<>();
        if (isTerminal()) return states;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Player.EMPTY) {
                    Player[][] newBoard = new Player[3][3];
                    for (int k = 0; k < 3; k++) {
                        System.arraycopy(board[k], 0, newBoard[k], 0, 3);
                    }
                    newBoard[i][j] = currentPlayer;
                    states.add(new GameState(newBoard, currentPlayer.opponent()));
                }
            }
        }
        return states;
    }

    public Player getCell(int row, int col) {
        return board[row][col];
    }
}