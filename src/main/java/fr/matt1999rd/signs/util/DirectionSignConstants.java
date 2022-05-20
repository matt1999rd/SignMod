package fr.matt1999rd.signs.util;

public class DirectionSignConstants {
    //for horizontal disposition of text in direction panel : 1(limit) 2(st gap) 144(beg text) 4(larger gap) 25(end text) 2(st gap) 1(limit)
    //if symmetry in panel just swap value of beg text and end text
    public static final int totalLength = 14;
    public static final int singlePanelHeight = 2;
    public static final int gapBtwPanelHeight = 2;
    public static final int totalHeight = 10;
    //pixel number on GUI
    public static final int horPixelNumber = 179;
    //pixel number on GUI
    public static final int verPixelNumber = 128;
    public static final int endTextPixelNumber= 25;
    public static final int sideGapPixelNumber = 3; //st gap+limit
    public static final int centerGapPixelNumber = 4; //larger gap
    public static final int begTextPixelNumber = horPixelNumber-endTextPixelNumber-2*sideGapPixelNumber-centerGapPixelNumber;
}
