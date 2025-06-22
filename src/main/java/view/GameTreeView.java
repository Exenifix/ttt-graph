package view;

import model.GameNode;
import model.GameState;
import model.Player;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.*;

public class GameTreeView extends ScrollPane {
    private static final double V_SPACING = 200;
    private static final double H_SPACING = 100;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 100;
    private static final double BOARD_SIZE = 50;
    private static final double CELL_SIZE = BOARD_SIZE / 3;
    private static final double PIECE_SIZE = CELL_SIZE * 0.6;

    private final Pane contentPane;

    private double lastX, lastY;

    public GameTreeView() {
        contentPane = new Pane();
        setContent(contentPane);
        setFitToWidth(true);
        setFitToHeight(true);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setStyle("-fx-background: white;");

        setupMousePanning();
    }

    private void setupMousePanning() {
        // Zoom on scroll
        contentPane.setOnScroll(event -> {
            double zoomFactor = Math.pow(1.01, -event.getDeltaY());
            contentPane.setScaleX(contentPane.getScaleX() * zoomFactor);
            contentPane.setScaleY(contentPane.getScaleY() * zoomFactor);
            event.consume();
        });

        // Store initial mouse position on press
        contentPane.setOnMousePressed(event -> {
            lastX = event.getSceneX();
            lastY = event.getSceneY();
            contentPane.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        // Calculate delta and scroll during drag
        contentPane.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - lastX;
            double deltaY = event.getSceneY() - lastY;

            // Adjust scroll values inversely to mouse movement
            setHvalue(getHvalue() - deltaX / contentPane.getWidth());
            setVvalue(getVvalue() - deltaY / contentPane.getHeight());

            lastX = event.getSceneX();
            lastY = event.getSceneY();
        });

        // Reset cursor on release
        contentPane.setOnMouseReleased(event -> {
            contentPane.setCursor(javafx.scene.Cursor.DEFAULT);
        });

        // Change cursor to hand when over the content pane
        contentPane.setOnMouseEntered(event -> {
            if (event.isPrimaryButtonDown()) {
                contentPane.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            } else {
                contentPane.setCursor(javafx.scene.Cursor.OPEN_HAND);
            }
        });

        contentPane.setOnMouseExited(event -> {
            if (!event.isPrimaryButtonDown()) {
                contentPane.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }

    public void drawTree(GameNode root) {
        contentPane.getChildren().clear();
        if (root == null) return;

        // Step 1: Populate layers
        List<List<GameNode>> layers = new ArrayList<>();
        Map<GameNode, Integer> depthMap = new HashMap<>();
        Map<GameNode, Point2D> nodePositions = new HashMap<>();
        List<Pair<GameNode, GameNode>> edges = new ArrayList<>();

        // BFS to assign depths and create layers
        Queue<GameNode> queue = new LinkedList<>();
        queue.add(root);
        depthMap.put(root, 0);

        while (!queue.isEmpty()) {
            GameNode current = queue.poll();
            int depth = depthMap.get(current);

            // Ensure layer exists
            if (depth >= layers.size()) {
                layers.add(new ArrayList<>());
            }
            layers.get(depth).add(current);

            for (GameNode child : current.getChildren()) {
                depthMap.put(child, depth + 1);
                queue.add(child);
                var pair = new Pair<>(current, child);
                if (!edges.contains(pair)) {
                    edges.add(pair);
                }
            }
        }

        // Step 2: Calculate positions for each layer
        double startY = 50;
        double maxTreeWidth = getMaxLayerWidth(layers) * H_SPACING;

        // Calculate positions from bottom up to center children under parents
        for (int depth = 0; depth < layers.size(); depth++) {
            List<GameNode> layer = layers.get(depth);
            double layerWidth = getLayerWidth(layer) * H_SPACING;
            double startX = (maxTreeWidth - layerWidth) / 2;

            // Position nodes in this layer
            int offset = 0;
            for (int i = 0; i < layer.size(); i++) {
                GameNode node = layer.get(i);
                double x = startX + (i - offset) * H_SPACING;
                double y = startY + depth * V_SPACING;
                System.out.printf("Drawing node: %.1f, %.1f", x, y);
                if (nodePositions.containsKey(node)) {
                    offset++;
                } else {
                    nodePositions.put(node, new Point2D(x, y));
                }
            }
        }

        // Step 3: Draw connections
        for (Pair<GameNode, GameNode> edge : edges) {
            Point2D startPos = nodePositions.get(edge.getKey());
            Point2D endPos = nodePositions.get(edge.getValue());

            if (startPos != null && endPos != null) {
                Line line = new Line(
                        startPos.getX() + NODE_WIDTH / 2,
                        startPos.getY() + NODE_HEIGHT,
                        endPos.getX() + NODE_WIDTH / 2,
                        endPos.getY()
                );
                line.setStroke(Color.LIGHTGRAY);
                contentPane.getChildren().add(line);
            }
        }

        // Step 4: Draw nodes
        for (GameNode node : nodePositions.keySet()) {
            Point2D position = nodePositions.get(node);
            Node nodeView = createGameNode(node, position);
            contentPane.getChildren().add(nodeView);
        }

        // Set content pane size for scrolling
        contentPane.setMinSize(maxTreeWidth + NODE_WIDTH,
                layers.size() * V_SPACING + NODE_HEIGHT + 100);
    }

    private double getLayerWidth(List<GameNode> nodes) {
        return (new HashSet<>(nodes)).size();
    }

    private double getMaxLayerWidth(List<List<GameNode>> layers) {
        double maxWidth = 0;
        double width;
        for (var l : layers) {
            if ((width = getLayerWidth(l)) > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private Node createGameNode(GameNode gameNode, Point2D position) {
        Group group = new Group();
        group.setLayoutX(position.getX());
        group.setLayoutY(position.getY());

        // Node background (opaque)
        Rectangle bg = new Rectangle(0, 0, NODE_WIDTH, NODE_HEIGHT);
        bg.setFill(Color.WHITE);
        bg.setStroke(Color.BLACK);
        bg.setArcWidth(10);
        bg.setArcHeight(10);
        group.getChildren().add(bg);

        // Mini board position
        double boardTop = 10;
        double boardLeft = (NODE_WIDTH - BOARD_SIZE) / 2;

        // Draw board grid
        drawBoardGrid(group, boardLeft, boardTop);

        // Draw X's and O's
        drawBoardState(group, gameNode.getState(), boardLeft, boardTop);

        // Win probability with opaque background
        Text probText = new Text(
                String.format("%.0f%%", gameNode.getWinProbability() * 100)
        );

        // Position text properly
        double textWidth = probText.getLayoutBounds().getWidth();
        double textX = (NODE_WIDTH - textWidth) / 2;
        double textY = NODE_HEIGHT - 10;
        probText.setX(textX);
        probText.setY(textY);

        // Add opaque background behind text
        Rectangle textBg = new Rectangle(
                textX - 2,
                textY - probText.getLayoutBounds().getHeight() + 2,
                textWidth + 4,
                probText.getLayoutBounds().getHeight() + 2
        );
        textBg.setFill(Color.WHITE);
        textBg.setStroke(Color.TRANSPARENT);

        group.getChildren().addAll(textBg, probText);

        return group;
    }

    private void drawBoardGrid(Group group, double left, double top) {
        // Draw the board background
        Rectangle boardBg = new Rectangle(left, top, BOARD_SIZE, BOARD_SIZE);
        boardBg.setFill(Color.WHITE);
        boardBg.setStroke(Color.BLACK);
        group.getChildren().add(boardBg);

        // Vertical lines
        for (int i = 1; i < 3; i++) {
            Line line = new Line(
                    left + i * CELL_SIZE,
                    top,
                    left + i * CELL_SIZE,
                    top + BOARD_SIZE
            );
            line.setStroke(Color.GRAY);
            group.getChildren().add(line);
        }

        // Horizontal lines
        for (int i = 1; i < 3; i++) {
            Line line = new Line(
                    left,
                    top + i * CELL_SIZE,
                    left + BOARD_SIZE,
                    top + i * CELL_SIZE
            );
            line.setStroke(Color.GRAY);
            group.getChildren().add(line);
        }
    }

    private void drawBoardState(Group group, GameState state, double boardLeft, double boardTop) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Player player = state.getCell(row, col);
                if (player != Player.EMPTY) {
                    double centerX = boardLeft + col * CELL_SIZE + CELL_SIZE / 2;
                    double centerY = boardTop + row * CELL_SIZE + CELL_SIZE / 2;

                    if (player == Player.X) {
                        // Draw X
                        Line line1 = new Line(
                                centerX - PIECE_SIZE / 2, centerY - PIECE_SIZE / 2,
                                centerX + PIECE_SIZE / 2, centerY + PIECE_SIZE / 2
                        );
                        Line line2 = new Line(
                                centerX + PIECE_SIZE / 2, centerY - PIECE_SIZE / 2,
                                centerX - PIECE_SIZE / 2, centerY + PIECE_SIZE / 2
                        );
                        line1.setStroke(Color.BLUE);
                        line1.setStrokeWidth(2);
                        line2.setStroke(Color.BLUE);
                        line2.setStrokeWidth(2);
                        group.getChildren().addAll(line1, line2);
                    } else {
                        // Draw O
                        Circle circle = new Circle(
                                centerX, centerY, PIECE_SIZE / 2
                        );
                        circle.setFill(Color.TRANSPARENT);
                        circle.setStroke(Color.RED);
                        circle.setStrokeWidth(2);
                        group.getChildren().add(circle);
                    }
                }
            }
        }
    }
}