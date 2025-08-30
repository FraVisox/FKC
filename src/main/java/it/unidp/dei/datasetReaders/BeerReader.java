package it.unidp.dei.datasetReaders;

import it.unidp.dei.Point;

//Reader of BEERS
public class BeerReader extends DatasetReader {
    @Override
    public Point nextPoint(int time, int wSize) {

        double[] coords = new double[dimension];
        for(int i=0; i<dimension; i++){
            coords[i] = reader.getDouble();
        }

        return new Point(coords, time, wSize, reader.getInt());
    }

    public static final int dimension = 5;
}
