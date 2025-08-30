package it.unidp.dei.datasetReaders;

import it.unidp.dei.Point;

//Reader of PHONES
public class PhonesReader3 extends DatasetReader {
    @Override
    public Point nextPoint(int time, int wSize) {
        //Skip the first line, as it has the header
        if (first) {
            reader.skipFirstLine();
            first = false;
        }

        //Index, Arrival_time, Creation_time: to be discarded
        for(int i=0; i<firstIgnored; i++) {
            reader.getWord();
        }

        //Coordinates to be saved (x,y,z)
        double[] coords = new double[dimension];
        for(int i=0; i<dimension; i++) {
            coords[i] = reader.getDouble();
        }

        //User, model, device: to be discarded
        for(int i=0; i<secondIgnored; i++) {
            reader.getWord();
        }

        //Category:
        int category = getCategory(reader.getWord());
        if (category == -1) {
            return null;
        }

        return new Point(coords, time, wSize, category);
    }

    //The activities are: bike, sit, stand, walk, stairsup, stairsdown and null
    private static int getCategory(String s) {
        switch (s) {
            case "bike":
            case "sit": return 0;
            case "walk":
            case "stairsup":
            case "stairsdown": return 1;
            case "stand":
            case "null": return 2;
            default: return -1;
        }
    }
    public static final int dimension = 3;
    public static final int firstIgnored = 3;
    public static final int secondIgnored = 3;
    private boolean first = true;
}
