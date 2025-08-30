package it.unidp.dei;

import it.unidp.dei.OURS.CAPPDELTAxx.*;
import it.unidp.dei.OURS.CAPP.*;
import it.unidp.dei.OURS.PELL.PELL;
import it.unidp.dei.OURS.PELL.PELLOBL;
import it.unidp.dei.CHENETAL.CHEN;
import it.unidp.dei.JONES.JONES;
import it.unidp.dei.datasetReaders.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import static it.unidp.dei.BlobsTestUtils.blobsKi;

//Methods called by Main.java to test PHONES, COVERTYPE, HIGGS
public class TestUtils {
    //Folders of input and output files
    public static final String inFolderOriginals = "data/originals/";
    public static final String inFolderRandomized = "data/randomized/";
    public static final String outFolder = "out/";

    //It tells how many times we will query the algorithms after wSize
    private static final int stride = 200;

    //Datasets: input files and output files, plus the DatasetReaders to read them
    private static final String[] datasets = {"Phones_accelerometer.csv", "uber.csv", "beers_10000.csv"};//{"twitter.csv"};//{"Phones_accelerometer.csv", "covtype.dat", "HIGGS.csv"};
    private static final String[] outFiles = {"TestPhones.csv", "TestUber.csv", "TestBeers.csv"};//{"TestTwitter.csv"};//{"TestPhones.csv", "TestCovtype.csv", "TestHiggs.csv"};
    private static final Class[] readers = {PhonesReader.class, UberReader.class, BeerReader.class};//{TwitterReader.class};//{PhonesReader.class, CovertypeReader.class, HiggsReader.class};

    //Some default parameters that are the same for every dataset
    public static final double defaultEpsilon = 0.9;
    private static final double[] defaultDeltas = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0};
    public static final double defaultBeta = 2;
    public static int defaultWSize = 30000;

    private static final int[][] defaultKi = {{4, 5, 4, 5, 4, 4, 4}, {1,6,5,17,1}, {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 2, 2, 5, 3, 2, 7}};//, {}, {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 2, 1, 1, 3}};//{{}, {14}, {6, 8}, {4, 6, 4}, {4, 2, 4, 4}, {2, 2, 2, 4, 4}, {2, 2, 2, 2, 4, 2}, {2, 2, 2, 2, 2, 2, 2}};//{1, 4, 3, 6, 0}};//{{5, 4, 5}};//{{2, 2, 2, 2, 2, 2, 2}, {5, 7, 1, 0, 0, 0, 1}, {7, 7}};
    public static final double INF = 8900;

    //VALUES OF MAX AND MIN DISTANCES (measured with CalculateMinMaxDist):
    //  PHONES: maxD = 52.6 and minD = 8.1e-5 (tested for 600000 points, and there are 13062475)
    //  COVTYPE: maxD = 8853.4 and minD = 2.82 (tested for all 581012 points)
    //  HIGGS: maxD = 26.7 and minD = 0.008 (tested for 600000 points and there are 11000000)
    private static final double[] minDist = {1e-4, 9e-5, 0.5};//{6.5e-7};//{8.1e-5, 2.82, 0.008};
    private static final double[] maxDist = {35.2, 1.38, 8.95};//{1.62};//{52.6, 8853.4, 26.7};

    //VALUES OF REAL MAX AND MIN DISTANCES for 10.000 points:
    private static final double[] realMinDist = {1e-4, 9e-5, 0.5};//{6.5e-7};//{0.002, 8.12, 0.02};
    private static final double[] realMaxDist = {35.2, 1.38, 8.95};//{1.62};//{33.7, 8693, 15.4};

    //Test of algorithms with standard parameters on randomized datasets
    public static void testRandomized() {
        testDatasets(true, null, defaultKi, defaultWSize, defaultBeta);
    }

    //Test of algorithms with standard parameters on originals datasets
    public static void testOriginals() {
        testDatasets(false, null, defaultKi, defaultWSize, defaultBeta);
    }

    //Test with different ki of standard datasets
    public static void testKi() {
        //The values are given as to preserve the number K but distributed according to the percentages of the points
        int[][][] ki = {
                //PHONES, COVTYPE, HIGGS
                {{1, 1, 1, 1, 1, 1, 1} },//, {3, 4, 0, 0, 0, 0, 0}, {1, 1}},
                {{2, 2, 2, 2, 2, 2, 2} },//, {5, 7, 1, 0, 0, 0, 1}, {2, 2}},
                {{3, 3, 3, 3, 3, 3, 3} },//, {5, 7, 1, 0, 0, 0, 1}, {2, 2}},
                {{5, 6, 5, 6, 4, 4, 5} },//, {13, 17, 2, 0, 1, 1, 1}, {9, 11}},
                {{10, 11, 9, 12, 9, 9, 10} },//, {25, 35, 4, 0, 1, 2, 3}, {14, 16}},
                {{15, 16, 14, 19, 13, 13, 15} },//, {37, 52, 6, 1, 2, 3, 4}, {19, 21}},
                {{20, 22, 19, 25, 18, 17, 19} },//, {50, 69, 9, 1, 2, 4, 5}, {24, 26}},
                {{25, 27, 24, 31, 22, 22, 24} },//, {62, 87, 11, 1, 3, 5, 6}, {28, 32}},
                //{{50, 55, 48, 62, 44, 43, 48} },//, {125, 173, 21, 2, 6, 11, 12}, {47, 53}},
                //{{99, 110, 95, 125, 88, 86, 97} },//, {250, 346, 43, 3, 12, 22, 24}, {94, 106}},
        };
        for (int[][] ints : ki) {
            int k = Algorithm.calcK(ints[0]);
            testDatasets(true, "k" + k, ints, defaultWSize, defaultBeta);
        }
    }

    public static void testK100() {
        testDatasets(true, "k100", defaultKi, defaultWSize, defaultBeta);
    }

    //Test on dataset of known radius obtained through datasetUtils.CreateAdHocDataset.py
    public static void testPerfectDataset() {
        RandomReader reader;
        PrintWriter writer;

        double min_distances = 2.27;
        double max_distances = 124.7;

        try {
            reader = new RandomReader(15);
            reader.setSource(inFolderRandomized + "perfect_dataset.csv");
            writer = new PrintWriter(outFolder + "test_perfect.csv");
        } catch (FileNotFoundException e) {
            System.out.println("File perfect_dataset.csv not found, skipping to next dataset");
            return;
        }

        testAlgorithms(reader, writer, blobsKi, defaultWSize, defaultEpsilon, defaultBeta, min_distances, max_distances);

        writer.close();

        reader.close();
    }

    public static void testDelta() {
        testDatasets(true, null, defaultKi, defaultWSize, defaultBeta);
    }

    //Test with different beta on standard datasets
    public static void testBeta() {
        double[] beta = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 5, 10, 15, 20, 30, 40, 50, 70, 100, 200, 500, 1000};
        for (double b : beta) {
            testDatasets(true, "beta" + b, defaultKi, defaultWSize, b);
        }
    }

    //Test with different wSize on standard datasets
    public static void testWSize() {
        int[] wSize = {10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000, 200000, 500000};
        for (int w : wSize) {
            testDatasets(true, "w" + w, defaultKi, w, defaultBeta);
            System.gc();
        }
    }

    public static void testCategories() {
        //int[] wSize = {10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000, 200000, 500000};
        DatasetReader[] readers = {new PhonesReader1(), new PhonesReader2(), new PhonesReader3(), new PhonesReader4(), new PhonesReader5(), new PhonesReader6(), new PhonesReader()};
        int cat = 1;
        for (DatasetReader reader : readers) {
            PrintWriter writer;

            String set = datasets[0];
            try {
                reader.setSource(inFolderRandomized + set);
                writer = new PrintWriter(outFolder + "randCat"+ cat + outFiles[0]);
            } catch (FileNotFoundException e) {
                System.out.println("File " + set + " not found, skipping to next dataset");
                continue;
            }

            System.out.println("Test categories "+cat+"\n");
            testDiffAlgorithms(reader, writer, minDist[0], maxDist[0], realMinDist[0], realMaxDist[0], defaultKi[cat], defaultWSize, TestUtils.defaultEpsilon, defaultBeta);

            //CLOSE
            writer.close();

            reader.close();
            System.out.println(set+" finished with categories: "+cat);
            cat++;
        }
    }

    //TEST DATASETS, called in every test with different parameters
    public static void testDatasets(boolean rand, String name, int[][] ki, int wSize, double beta) {
        DatasetReader reader;
        PrintWriter writer;

        //For every different parameter passed, we make tests on all datasets
        for (int i = 0; i< datasets.length; i++) {
            String set = datasets[i];

            try {
                reader = (DatasetReader) readers[i].newInstance();
                if (i == 0) {
                    reader.setSource(inFolderRandomized+set);
                } else {
                    reader.setSource(inFolderOriginals + set);
                }
                writer = new PrintWriter(outFolder + "rand" + name + outFiles[i]);
            } catch (FileNotFoundException e) {
                System.out.println("File " + set + " not found, skipping to next dataset");
                continue;
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Problem with the search of the right reader for the "+set+"dataset");
                continue;
            }

            //Depending on deltas, call the testings
            if (name == null) {
                //THIS IS ONLY FOR RANDOM AND ORIGINAL
                System.out.println("Test delta\n\n");
                testDiffAlgorithms(reader, writer, minDist[i], maxDist[i], realMinDist[i], realMaxDist[i], ki[i], wSize, TestUtils.defaultEpsilon, beta);
            } else {
                System.out.println("Test wsize\n\n");
                testAlgorithms(reader, writer, ki[i], wSize, defaultEpsilon, beta, minDist[i], maxDist[i]);
            }

            //CLOSE
            writer.close();

            reader.close();
            System.out.println(set+" finished");
        }
    }

    //Test on rotated dataset obtained from Phones through datasetUtils.RotatePhones.py
    public static void testRotatedPhones() {
        int[] dimensions = {3,6,9,12,15};
        for (int d : dimensions) {
            testRotated(d);
        }
    }

    private static void testRotated(int dim) {
        RotatedPhonesReader reader;
        PrintWriter writer;

        double min_distances = 1.2e-4;
        double max_distances = 47.6;

        //For every different parameter passed, we make tests on all datasets
        try {
            reader = new RotatedPhonesReader();
            reader.setSource(inFolderRandomized + "phones_"+dim+".csv");
            reader.setDimension(dim);
            writer = new PrintWriter(outFolder + "test_rotated_"+dim+".csv");
        } catch (FileNotFoundException e) {
            System.out.println("File phones_" + dim + ".csv not found, skipping to next dataset");
            return;
        }

        testAlgorithms(reader, writer, defaultKi[0], defaultWSize, defaultEpsilon, defaultBeta, min_distances, max_distances);

        writer.close();

        reader.close();
        System.out.println("phones_rotated_"+dim+" finished");
    }


    //Test on standard datasets for fair and unfair algorithms
    public static void testPriceOfFairness() {
        DatasetReader reader;
        PrintWriter writer;

        //For every different parameter passed, we make tests on all datasets
        for (int i = 0; i < 3; i++) {
            String set = datasets[i];
            try {
                //Create a dataset reader
                reader = (DatasetReader) readers[i].newInstance();

                //Instantiate the file
                if (reader instanceof HiggsReader) {
                    reader.setSource(inFolderOriginals + set);
                } else {
                    reader.setSource(inFolderRandomized + set);
                }

                //Create a results writer
                writer = new PrintWriter(outFolder + "price_of_fairness_" + outFiles[i]);

            } catch (FileNotFoundException e) {
                System.out.println("File " + set + " not found, skipping to next dataset");
                continue;
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Problem with the search of the right reader for the "+set+"dataset");
                continue;
            }

            //Depending on deltas, call the testings
            testFairness(reader, writer, defaultKi[i], defaultWSize, defaultEpsilon, defaultBeta, minDist[i], maxDist[i]);

            //CLOSE
            writer.close();

            reader.close();
            System.out.println(set+" finished");
        }
    }

    private static void testFairness(DatasetReader reader, PrintWriter writer, int[] kiSet, int wSize, double epsilon, double beta, double minDist, double maxDist) {

        //Testing LinkedList, contains all the window
        LinkedList<Point> window = new LinkedList<>();

        Algorithm[] algorithms;
        //DEFAULT, with everything
        algorithms = new Algorithm[8];
        algorithms[0] = new PELLOBL(kiSet, 0.5, beta);
        algorithms[1] = new PELL(kiSet, 0.5, beta, minDist, maxDist);
        algorithms[2] = new PELLOBL(kiSet, 2.0, beta);
        algorithms[3] = new PELL(kiSet, 2.0, beta, minDist, maxDist);
        algorithms[4] = new PELLCAPPDELTAxx(beta, 0.5, kiSet);
        algorithms[5] = new CAPPDELTAxx(kiSet, 0.5, beta, minDist, maxDist);
        algorithms[6] = new PELLCAPPDELTAxx(beta, 2, kiSet);
        algorithms[7] = new CAPPDELTAxx(kiSet, 2, beta, minDist, maxDist);

        writer.println("PELLOBL05;;;;;;PELL05;;;;;;PELLOBL20;;;;;;PELL20;;;;;;PELLCAPPDELTA05;;;;;;CAPPDELTA05;;;;;;PELLCAPPDELTA20;;;;;;CAPPDELTA20;;;;;;");


        int i;
        String header = "Update Time;Query Time;Radius;Ratio;Memory";
        for (i = 0; i<algorithms.length; i++) {
            writer.print(header);
            writer.print(";;");
        }
        writer.println();

        for (int time = 1; time <= wSize+stride && reader.hasNext(); time++) {
            Point p = reader.nextPoint(time, wSize);

            if (p == null) {
                System.out.println("NULL POINT");
                continue;
            }

            //Update the window
            window.addLast(p);

            //If window is not full, we don't query
            if (time <= wSize) {
                for (Algorithm alg : algorithms) {
                    alg.update(p, time);
                }
                continue;
            }

            if (time % 50 == 0) {
                //Check of passing of time
                System.out.println(time);
            }

            window.removeFirst();

            double minR = -1;

            //Tests
            i = 0;
            for (Algorithm algorithm : algorithms) {
                calcUpdateTime(algorithm, p, time, writer);
                if (i == 0) {
                    minR = calcQuery(algorithm, writer, window, kiSet, -1);
                }
                else {
                    calcQuery(algorithm, writer, window, kiSet, minR);
                }
                calcMemory(algorithm, writer);
                writer.print(";;");
                i++;
            }
            writer.println();

            //FLUSH
            writer.flush();
        }
    }

    //Function called by everything else
    public static void testAlgorithms(DatasetReader reader, PrintWriter writer, int[] kiSet, int wSize, double epsilon, double beta, double minDist, double maxDist) {

        if (reader instanceof BeerReader) {
            switch (wSize) {
                case 10000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 5, 3, 2, 7};
                    break;

                case 20000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2, 3, 5, 2, 2, 7};
                    break;

                case 30000:

                case 40000:

                case 50000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 2, 2, 5, 3, 2, 7};
                    break;

                case 60000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2, 2, 5, 3, 2, 7};
                    break;

                case 70000:

                case 80000:

                case 90000:

                case 100000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2, 2, 4, 3, 2, 7};
                    break;

                case 200000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 2, 4, 3, 3, 8};
                    break;

                case 500000:
                    kiSet = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 3, 3, 8};
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported wsize: " + wSize);
            }
        } else if (reader instanceof UberReader) {

            switch (wSize) {
                case 10000:

                case 20000:

                case 30000:

                case 40000:

                case 50000:

                case 70000:

                case 90000:

                case 100000:
                    kiSet = new int[]{1,6,5,17,1};
                    break;

                case 60000:
                    kiSet = new int[]{0,5,5,19,1};
                    break;

                case 80000:

                case 200000:

                case 500000:
                    kiSet = new int[]{0,6,5,18,1};
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported wsize: " + wSize);
            }

        }


        //Testing LinkedList, contains all the window
        LinkedList<Point> window = new LinkedList<>();

        Algorithm[] algorithms;
        if (wSize >= 40000 && wSize <= 200000)
        {
            //NO CHEN
            algorithms = new Algorithm[3];
            algorithms[0] = new JONES(kiSet);

            algorithms[1] = new PELLCAPPDELTAxx(beta, 0.5, kiSet);
            algorithms[2] = new CAPPDELTAxx(kiSet, 0.5, beta, minDist, maxDist);

            writer.println("JONES;;;;;;PELLCAPPDELTA05;;;;;;CAPPDELTA05;;;;;;");
        } else if (wSize > 200000) {
            //NO JONES and NO CHEN
            algorithms = new Algorithm[2];

            algorithms[0] = new PELLCAPPDELTAxx(beta, 0.5, kiSet);
            algorithms[1] = new CAPPDELTAxx(kiSet, 0.5, beta, minDist, maxDist);

            writer.println("PELLCAPPDELTA05;;;;;;CAPPDELTA05;;;;;;");
        } else {
            //DEFAULT, with everything. These are not used for all tests, but only for the last ones made.
            algorithms = new Algorithm[4];
            algorithms[0] = new JONES(kiSet);
            algorithms[1] = new CHEN(kiSet);
            algorithms[2] = new PELLCAPPDELTAxx(beta, 0.5, kiSet);
            algorithms[3] = new CAPPDELTAxx(kiSet, 0.5, beta, minDist, maxDist);

            writer.println("JONES;;;;;;CHEN;;;;;;PELLCAPPDELTA05;;;;;;CAPPDELTA05;;;;;;");
        }

        int i;
        String header = "Update Time;Query Time;Radius;Ratio;Memory";
        for (i = 0; i<algorithms.length; i++) {
            writer.print(header);
            writer.print(";;");
        }
        writer.println();

        for (int time = 1; time <= wSize+stride && reader.hasNext(); time++) {
            Point p = reader.nextPoint(time, wSize);

            if (p == null) {
                System.out.println("NULL POINT");
                continue;
            }

            //Update the window
            window.addLast(p);

            //If window is not full, we don't query
            if (time <= wSize) {
                for (Algorithm alg : algorithms) {
                    alg.update(p, time);
                }
                continue;
            }

            if (time % 50 == 0) {
                //Check of passing of time
                System.out.println(time);
            }

            window.removeFirst();

            double minR = -1;

            //Tests
            i = 0;
            for (Algorithm algorithm : algorithms) {
                calcUpdateTime(algorithm, p, time, writer);
                if (i == 0) {
                    minR = calcQuery(algorithm, writer, window, kiSet, -1);
                }
                else {
                    calcQuery(algorithm, writer, window, kiSet, minR);
                }
                calcMemory(algorithm, writer);
                writer.print(";;");
                i++;
            }
            writer.println();

            //FLUSH
            writer.flush();
        }
    }

    //Test of differences between datasets (originals and randomized).

    //In every line of the output file we will have a header:
    //Update Time;Query Time;Radius;Ratio;Memory
    public static void testDiffAlgorithms(DatasetReader reader, PrintWriter writer, double min, double max, double realMin, double realMax, int[] kiSet, int wSize, double epsilon, double beta) {

        //Testing LinkedList, contains all the window
        LinkedList<Point> window = new LinkedList<>();

        //Initialize the algorithms
        Algorithm[] algorithms = new Algorithm[18];
        algorithms[0] = new JONES(kiSet);
        algorithms[1] = new CHEN(kiSet);

        int i = 2;

        for (double dd : defaultDeltas) {
            algorithms[i] = new PELLCAPPDELTAxx(beta, dd, kiSet);
            i++;
        }

        for (double dd : defaultDeltas) {
            algorithms[i] = new CAPPDELTAxx(kiSet, dd, beta, min, max);
            i++;
        }

        writer.println("JONES;;;;;;CHEN;;;;;;PELLCAPPDELTA05;;;;;;PELLCAPPDELTA10;;;;;;PELLCAPPDELTA15;;;;;;PELLCAPPDELTA20;;;;;;PELLCAPPDELTA25;;;;;;PELLCAPPDELTA30;;;;;;PELLCAPPDELTA35;;;;;;PELLCAPPDELTA40;;;;;;CAPPDELTA05;;;;;;CAPPDELTA10;;;;;;CAPPDELTA15;;;;;;CAPPDELTA20;;;;;;CAPPDELTA25;;;;;;CAPPDELTA30;;;;;;CAPPDELTA35;;;;;;CAPPDELTA40;;;;;;");

        String header = "Update Time;Query Time;Radius;Ratio;Memory";
        for (i = 0; i<algorithms.length; i++) {
            writer.print(header);
            writer.print(";;");
        }
        writer.println();

        for (int time = 1; time <= wSize+stride && reader.hasNext(); time++) {
            Point p = reader.nextPoint(time, wSize);

            if (p == null) {
                System.out.println("NULL POINT");
                continue;
            }

            //Update the window
            window.addLast(p);

            //If window is not full, we don't query
            if (time <= wSize) {
                for (Algorithm alg : algorithms) {
                    alg.update(p, time);
                }
                continue;
            }

            if (time % 50 == 0) {
                System.out.println(time);
            }

            window.removeFirst();


            double minR = -1;

            //Tests
            i = 0;
            for (Algorithm algorithm : algorithms) {
                calcUpdateTime(algorithm, p, time, writer);
                if (i == 0) {
                    minR = calcQuery(algorithm, writer, window, kiSet, -1);
                }
                else {
                    calcQuery(algorithm, writer, window, kiSet, minR);
                }
                calcMemory(algorithm, writer);
                writer.print(";;");
                i++;
            }
            writer.println();

            //FLUSH
            writer.flush();
        }
    }

    //PRIVATE METHODS to calculate time, memory and radius/ratio of the algorithms

    private static void calcUpdateTime(Algorithm alg, Point p, int time, PrintWriter writer) {
        //TIME TEST: we call explicitly the garbage collector to allow our algorithm
        //           to run without having to wait for the garbage collector
        long startTime, endTime;
        System.gc();
        startTime = System.nanoTime();
        alg.update(p, time);
        endTime = System.nanoTime();
        //Write on file the time of update
        writer.print((endTime-startTime)+";");
    }

    private static double calcQuery(Algorithm alg, PrintWriter writer, LinkedList<Point> window, int[] kiSet, double minRadius) {
        ArrayList<Point> centers;
        long startTime, endTime;

        //1. TIME TEST: we call explicitly the garbage collector to allow our algorithm
        //              to run without having to wait for the garbage collector
        System.gc();
        startTime = System.nanoTime();
        centers = alg.query();
        endTime = System.nanoTime();
        writer.print((endTime-startTime)+";");

        if (centers == null) {
            throw new RuntimeException("Max or min distances are not correct. There isn't a valid guess");
        }

        //2. QUALITY TEST: Check of the radius of the centers returned and the independence of the set
        double radius = maxDistanceBetweenSets(window, centers);

        if (!isIndependent(centers, kiSet)) {
            throw new RuntimeException(alg.getClass()+" did not solve the problem correctly");
        }

        writer.print(String.format(Locale.ITALIAN, "%.16f", radius)+";");
        if (minRadius == -1) {
            writer.print(String.format(Locale.ITALIAN, "%.16f", 1.0) + ";");
        } else {
            writer.print(String.format(Locale.ITALIAN, "%.16f", radius / minRadius) + ";");
        }
        return radius;
    }

    private static void calcMemory(Algorithm alg, PrintWriter writer) {
        writer.print(alg.getSize());
    }

    private static double maxDistanceBetweenSets(Collection<Point> set, Collection<Point> centers){
        double ans = 0;
        for(Point p : set){
            ans = Math.max(p.getMinDistance(centers), ans);
        }
        return ans;
    }

    private static boolean isIndependent(Collection<Point> set, int[] kiSet) {
        int[] kj = kiSet.clone();
        for (Point p : set) {
            kj[p.getGroup()] -= 1;
            if (kj[p.getGroup()] < 0) {
                return false;
            }
        }
        return true;
    }
}
