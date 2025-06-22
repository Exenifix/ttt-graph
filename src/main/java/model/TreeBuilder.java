package model;

import java.util.*;

public class TreeBuilder {
    public GameNode buildTree(GameState rootState) {
        Player rootPlayer = rootState.getCurrentPlayer();
        GameNode rootNode = new GameNode(rootState);
        Map<String, GameNode> visited = new HashMap<>();

        Queue<GameNode> queue = new LinkedList<>();
        queue.add(rootNode);
        visited.put(stateToString(rootState), rootNode);

        while (!queue.isEmpty()) {
            GameNode currentNode = queue.poll();
            GameState currentState = currentNode.getState();

            if (currentState.isTerminal()) {
                setTerminalProbability(currentNode, rootPlayer);
                continue;
            }

            for (GameState nextState : currentState.getNextStates()) {
                String stateKey = stateToString(nextState);
                GameNode childNode = visited.getOrDefault(stateKey, new GameNode(nextState));

                if (!visited.containsKey(stateKey)) {
                    visited.put(stateKey, childNode);
                    queue.add(childNode);
                }
                currentNode.getChildren().add(childNode);
            }
        }

        // Calculate win probabilities using BFS from leaves to root
        calculateProbabilities(rootNode, rootPlayer);
        return rootNode;
    }

    private void setTerminalProbability(GameNode node, Player rootPlayer) {
        Player winner = node.getState().getWinner();
        if (winner == rootPlayer) {
            node.setWinProbability(1.0);
        } else if (winner != null) {
            node.setWinProbability(0.0);
        } else { // Draw
            node.setWinProbability(0.0);
        }
    }

    private void calculateProbabilities(GameNode root, Player rootPlayer) {
        // Reverse BFS (from leaves to root)
        List<GameNode> allNodes = new ArrayList<>();
        Queue<GameNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            GameNode node = queue.poll();
            allNodes.add(node);
            queue.addAll(node.getChildren());
        }

        // Process from leaves to root
        Collections.reverse(allNodes);

        for (GameNode node : allNodes) {
            if (!node.getChildren().isEmpty()) {
                double sum = node.getChildren().stream()
                        .mapToDouble(GameNode::getWinProbability)
                        .sum();
                node.setWinProbability(sum / node.getChildren().size());
            }
        }
    }

    private String stateToString(GameState state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(state.getCell(i, j)).append(",");
            }
        }
        return sb.toString();
    }
}