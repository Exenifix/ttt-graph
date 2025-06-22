import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import model.GameNode;
import model.GameState;
import model.Player;
import model.TreeBuilder;
import view.BoardInputPane;
import view.GameTreeView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private ProgressIndicator progress;

    @Override
    public void start(Stage stage) {
        // Create UI components
        BoardInputPane boardInput = new BoardInputPane();
        GameTreeView treeView = new GameTreeView();
        ComboBox<Player> playerCombo = new ComboBox<>();
        Button generateBtn = new Button("Generate Tree");
        Button resetBtn = new Button("Reset Board");

        // Setup player selection
        playerCombo.getItems().addAll(Player.X, Player.O);
        playerCombo.setValue(Player.X);

        // Generate tree action
        generateBtn.setOnAction(e -> {
            GameState initialState = new GameState(
                    boardInput.getBoard(),
                    playerCombo.getValue()
            );
            treeView.drawTree(new TreeBuilder().buildTree(initialState));
        });

        // Reset board action
        resetBtn.setOnAction(e -> boardInput.reset());

        // Assemble UI
        HBox controls = new HBox(10,
                new Label("Current Player:"), playerCombo,
                generateBtn, resetBtn
        );
        controls.setPadding(new Insets(10));

        VBox inputPanel = new VBox(10, boardInput, controls);
        inputPanel.setPadding(new Insets(10));

        generateBtn.setOnAction(e -> {
            progress.setVisible(true);

            Task<GameNode> buildTreeTask = new Task<>() {
                @Override
                protected GameNode call() {
                    GameState initialState = new GameState(
                            boardInput.getBoard(),
                            playerCombo.getValue()
                    );
                    return new TreeBuilder().buildTree(initialState);
                }
            };

            buildTreeTask.setOnSucceeded(evt -> {
                treeView.drawTree(buildTreeTask.getValue());
                progress.setVisible(false);
            });

            buildTreeTask.setOnFailed(evt -> {
                buildTreeTask.getException().printStackTrace();
                progress.setVisible(false);
            });

            new Thread(buildTreeTask).start();
        });

        // Add progress indicator to UI
        progress = new ProgressIndicator();
        progress.setVisible(false);
        progress.setPrefSize(30, 30);

        HBox progressBox = new HBox(progress);
        progressBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(inputPanel);
        root.setCenter(new VBox(treeView, progressBox));

        // Configure stage
        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Tic Tac Toe Game Tree");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}