package it.unidp.dei.datasetReaders;

import it.unidp.dei.Point;

//Reader of TWITTER
public class TwitterReader extends DatasetReader {
    @Override
    public Point nextPoint(int time, int wSize) {
        int sex = reader.getInt();
        double[] coords = new double[dimension];
        for(int i=0; i<dimension; i++){
            coords[i] = reader.getDouble();
        }

        return new Point(coords, time, wSize, sex);
    }
    public static final int dimension = 1024;
}
