package view;

import model.Player;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class BoardInputPane extends GridPane {
    private Player[][] board = new Player[3][3];
    private final Button[][] buttons = new Button[3][3];

    public BoardInputPane() {
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = Player.EMPTY;
                Button button = new Button();
                button.setPrefSize(50, 50);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(e -> handleButtonClick(finalRow, finalCol));
                buttons[row][col] = button;
                add(button, col, row);
            }
        }
    }

    private void handleButtonClick(int row, int col) {
        Player current = board[row][col];
        board[row][col] = switch (current) {
            case EMPTY -> Player.X;
            case X -> Player.O;
            case O -> Player.EMPTY;
        };
        buttons[row][col].setText(
                board[row][col] != Player.EMPTY ? board[row][col].toString() : ""
        );
    }

    public Player[][] getBoard() {
        return board;
    }

    public void reset() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = Player.EMPTY;
                buttons[row][col].setText("");
            }
        }
    }
}
