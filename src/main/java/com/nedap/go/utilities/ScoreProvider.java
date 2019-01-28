package com.nedap.go.utilities;

import javafx.util.Pair;

import java.util.*;

public class ScoreProvider {
    private Board board;
    private static final double DEFAULT_KOMI = 0.5;

    public ScoreProvider (Board board) {
        this.board = board;
    }

    public Map<TileColour, Double> getScore() {
        Map<TileColour, Double> score = new HashMap<>();
        List<TileColour> players = this.board.turnKeeper().players();

        players.forEach(colour -> score.put(colour, 0.0));

        // count occupied tiles and captured area
        score.keySet().forEach(tileColour -> {
                    score.put(tileColour, score.get(tileColour) + countTilesOfColour(tileColour));
                    score.put(tileColour, score.get(tileColour) + countCapturedArea(tileColour));
                }
        );

        // add Komi points
        score.put(TileColour.WHITE, score.get(TileColour.WHITE) + this.komi());

        return score;
    }

    private int countTilesOfColour (TileColour colour) {
        Set<Integer> tilesOfColour = this.board.extractTilesOfColour(colour);

        return tilesOfColour.size();
    }

    private int countCapturedArea (TileColour colour) {
        Set<Integer> uncheckedEmptyTiles = this.board.extractTilesOfColour(TileColour.EMPTY);
        Set<Integer> capturedTiles = new HashSet<>();
        Set<Integer> unCapturedTiles = new HashSet<>();

        while(uncheckedEmptyTiles.size() > 0) {
            Pair<Boolean, Set<Integer>> consideredTiles = checkGroupFromTile(
                    colour,
                    (int) uncheckedEmptyTiles.toArray()[0], // cast to array to easily pick one, order is irrelevant
                    null
            );

            if(consideredTiles.getKey()) {
                capturedTiles.addAll(consideredTiles.getValue());
            } else {
                unCapturedTiles.addAll(consideredTiles.getValue());
            }

            uncheckedEmptyTiles.removeAll(consideredTiles.getValue());
        }

        return capturedTiles.size();
    }

    private Pair<Boolean, Set<Integer>> checkGroupFromTile(TileColour colour, int index, Set<Integer> group) {
        Set<Integer> captureGroup = group != null ? group : new HashSet<>(Arrays.asList(index));
        Set<Integer> neighbours = this.board.getNeighbourIndices(index);
        boolean groupCaptured = true;

        for (int neighbourIndex: neighbours) {
            // already considered
            if (captureGroup.contains(neighbourIndex)) {
                continue;
            }

            // neighbour is empty add to group
            if (this.board.currentState().charAt(neighbourIndex) == TileColour.EMPTY.asChar()) {
                captureGroup.add(neighbourIndex);
                Pair<Boolean, Set<Integer>> neighbourResult = checkGroupFromTile(colour, neighbourIndex, captureGroup);

                captureGroup.addAll(neighbourResult.getValue());
                groupCaptured = groupCaptured && neighbourResult.getKey();
                continue;
            }

            // neighbour is of same colour
            if (this.board.currentState().charAt(neighbourIndex) == colour.asChar()) {
                continue;
            }

            groupCaptured = false;

        }

        return new Pair<>(groupCaptured, captureGroup);
    }




    private double komi() {
        // TODO: implement based on board size/config if we decide to add this to the protocol

        return DEFAULT_KOMI;
    }



}
