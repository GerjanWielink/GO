package com.nedap.go.utilities;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private TileColour colour;
    private Set<Integer> group;
    private Set<Integer> neighbours;
    private Set<Integer> freedoms;

    public Group(TileColour colour, Set<Integer> group, Set<Integer> neighbours, Set<Integer> freedoms) {
        this.colour = colour;
        this.group = group;
        this.neighbours = neighbours;
        this.freedoms = freedoms;
    }

    public boolean considers(Integer index) {
        return this.group.contains(index)
                || this.neighbours.contains(index)
                || this.freedoms.contains(index);
    }

    public boolean isCaptured() {
        return this.freedoms.size() == 0 && this.neighbours.size() > 0;
    }

    public void merge(Group mergeGroup) {
        if (mergeGroup.colour() != this.colour) {
            return;
        }

        this.neighbours.addAll(mergeGroup.neighbours());
        this.group.addAll(mergeGroup.group());
        this.freedoms.addAll(mergeGroup.freedoms());
    }

    /**
     * "Flooding" algorithm to fetch the group
     * @param boardState
     * @param index
     * @return
     */
    public static Group getGroupFromIndex(String boardState, Integer index) {
        TileColour colourSelf = TileColour.EMPTY;

        for (TileColour tileColour: TileColour.values()) {
            if (tileColour.asChar() == boardState.charAt(index)) {
                colourSelf = tileColour;
                break;
            }
        }

        Set<Integer> selfSet = new HashSet<>();
        selfSet.add(index);

        Group initialGroup = new Group(
                colourSelf,
                selfSet,
                new HashSet<>(),
                new HashSet<>()
        );

        return getGroupFromIndex(boardState, index, colourSelf, initialGroup);
    }

    public static Group getGroupFromIndex(String boardState, Integer index, TileColour colour, Group group) {
        Set<Integer> neighbourIndices = Board.getNeighbourIndices(index, (int) Math.sqrt(boardState.length()));


        for(int neighbourIndex: neighbourIndices) {
            if (group.considers(neighbourIndex)){
                continue;
            }

            if(boardState.charAt(neighbourIndex) == colour.asChar()) {
                group.addGroupMember(neighbourIndex);
                group.merge(getGroupFromIndex(boardState, neighbourIndex, colour, group));
                continue;
            }

            if(boardState.charAt(neighbourIndex) == TileColour.EMPTY.asChar()) {
                group.addFreedom(neighbourIndex);
                continue;
            }

            group.addNeighbour(neighbourIndex);
        }

        return group;
    }

    public void addFreedom(Integer index) {
        this.freedoms.add(index);
    }

    public void addGroupMember(Integer index) {
        this.group.add(index);
    }

    public void addNeighbour(Integer index) {
        this.neighbours.add(index);
    }

    public Set<Integer> group() {
        return this.group;
    }

    public Set<Integer> neighbours() {
        return this.neighbours;
    }

    public Set<Integer> freedoms() {
        return this.freedoms;
    }

    public TileColour colour() {
        return this.colour;
    }
}

