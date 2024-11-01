package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;

public class HideAndSeekPlayer {

    public enum Placement {
        DNF("DNF", 0),    // no points
        FIRST("First", 1),  // all the points
        SECOND("Second", 2), // 2 points
        THIRD("Third", 3),  // 1 point
        OTHER("Other", 4);   // no points

        final String name;
        final int val;

        Placement(String name, int placementVal)
        {
            this.name = name;
            this.val = placementVal;
        }

        public String toString()
        {
            return this.name;
        }

        public int getValue()
        {
            return this.val;
        }

        public static Placement fromValue(Integer value)
        {
            if (value == null)
            {
                return Placement.DNF;
            }
            switch (value)
            {
                case 0: return Placement.DNF;
                case 1: return Placement.FIRST;
                case 2: return Placement.SECOND;
                case 3: return Placement.THIRD;
                default: return Placement.OTHER;
            }
        }
    }

    @Getter
    private final String  name;
    @Getter
    private int internalPlacement;
    private Placement placement;
    @Getter
    private int hints;

    public HideAndSeekPlayer(String name) {
        this.name = name;
        this.internalPlacement = 0;
        this.placement = Placement.DNF;
        this.hints = 0;
    }

    public String getPlacementExportString()
    {
        return placement.toString();
    }

    public Placement getPlacementTableString()
    {
        return placement;
    }

    public void setStats(int internal_placement, int placement, int hints)
    {
        this.internalPlacement = internal_placement;
        this.placement = Placement.fromValue(placement);
        this.hints = hints;
    }

    public boolean hasPlaced()
    {
        return internalPlacement > 0;
    }

    public void reset()
    {
        internalPlacement = 0;
        placement = Placement.DNF;
        hints = 0;
    }

    public Object getValue(int index)
    {
        switch (index)
        {
            case 0:
                return getInternalPlacement();
            case 1:
                return getName();
            case 2:
                return getPlacementTableString();
            case 3:
                return getHints();
            case 4:
                return "+" + getScore();
        }
        return null;
    }

    private int getScore()
    {
        if (hints == 0 || placement == Placement.DNF)
            return 0;

        int placementValue = placement.getValue();

        int hintScore = 4 - hints;
        int placementScore = 1;
        if (placementValue <= 3)
        {
            placementScore = 5 - placementValue;
        }
        return hintScore + placementScore;

    }
}

//== Hints ==
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//1
//DNF
//DNF
//DNF
//DNF
//DNF
//1
//1
//== placement ==
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//DNF
//FIRST
//DNF
//DNF
//DNF
//DNF
//DNF
//SECOND
//SECOND
//== END ==

