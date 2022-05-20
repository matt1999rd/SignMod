package fr.matt1999rd.signs.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuadPSPositions {
    private final ArrayList<QuadPSPosition> positions;
    public QuadPSPositions(QuadPSPosition... positions){
        this.positions = Lists.newArrayList(positions);
    }

    public Vector2i getPosition(int ind){
        List<Integer> maxTexts = this.positions.stream().map(quadPSPosition -> quadPSPosition.maxText).collect(Collectors.toList());
        int accumulator = 0;
        int oldAccumulator = 0;
        int index = 0;
        while (accumulator<=ind && index<maxTexts.size()){
            oldAccumulator = accumulator;
            accumulator += maxTexts.get(index);
            index++;
        }
        index--; // when exiting this loop, index is 1 more than expected.
        QuadPSPosition position = this.positions.get(index);
        return position.makePosition(ind - oldAccumulator);
    }

    public QuadPSPosition getQuadPSPosition(int ind){
        List<Integer> maxTexts = this.positions.stream().map(quadPSPosition -> quadPSPosition.maxText).collect(Collectors.toList());
        int accumulator = 0;
        int index = 0;
        while (accumulator<=ind && index<maxTexts.size()){
            accumulator += maxTexts.get(index);
            index++;
        }
        index--; // when exiting this loop, index is 1 more than expected.
        return this.positions.get(index);
    }

    public static class QuadPSPosition {
        Vector2i begPosition;
        //maxLength is the maximum length of a text in this area
        //maxText is the number of total text contained in this object
        private final int maxLength;
        private final int maxText;
        public QuadPSPosition(int xBeg,int yBeg,int maxLength,int maxText){
            this.begPosition = new Vector2i(xBeg,yBeg);
            this.maxLength = maxLength;
            this.maxText = maxText;
        }

        public int getLengthMax(){ return maxLength; }

        public Vector2i makePosition(int relativeIndices){
            return new Vector2i(begPosition.getX(), begPosition.getY() + 9*relativeIndices);
        }
    }

}

