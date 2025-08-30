package it.unidp.dei.datasetReaders;

import it.unidp.dei.Point;

//Reader of UBER
public class UberReader extends DatasetReader {
    @Override
    public Point nextPoint(int time, int wSize) {
        reader.getWord();
        reader.getWord();
        double[] coords = new double[dimension];
        for(int i=0; i<dimension; i++){
            coords[i] = reader.getDouble();
        }

        return new Point(coords, time, wSize, getCategory(reader.getWord()));
    }

    /*
    B02512	Unter
    B02598	Hinter
    B02617	Weiter
    B02682	Schmecken
    B02764	Danach-NY
    B02765	Grun
    B02835	Dreist
    B02836	Drinnen
     */

    private static int getCategory(String s) {
        switch (s) {
            case "B02512": return 0;
            case "B02598": return 1;
            case "B02617": return 2;
            case "B02682": return 3;
            case "B02764": return 4;
            /* THEY DON'T EXIST
            case "B02765": return 5;
            case "B02835": return 6;
            case "B02836": return 7;
             */
            default: return -1;
        }
    }
    public static final int dimension = 2;
}
