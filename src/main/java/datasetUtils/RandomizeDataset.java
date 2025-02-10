package datasetUtils;

import it.unidp.dei.datasetReaders.InputFileReader;
import it.unidp.dei.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

//Randomize the dataset by simply swapping the lines
public class RandomizeDataset {
    private static final String outFolder = TestUtils.inFolderRandomized;
    private static final String inFolder = TestUtils.inFolderOriginals;

    //HIGGS does not need to be randomized, as it is already randomized
    private static final String[] datasets = {"Phones_accelerometer.csv", "covtype.dat"};

    //Tells if there is the header in the csv file
    private static final boolean[] isThereFirst = {true, false};

    public static void main(String[] args) {
        InputFileReader reader;
        PrintWriter writer;
        for (int i = 0; i<datasets.length; i++) {
            String set = datasets[i];
            try {
                //Create a file reader
                reader = new InputFileReader(inFolder+set);
                //Create a file writer
                writer = new PrintWriter(outFolder+set);
            } catch (FileNotFoundException e) {
                System.out.println("File " + inFolder + set + " not found, skipping to next dataset");
                continue;
            }

            ArrayList<String> strings = new ArrayList<>();

            //Takes all the lines (except the first if we are in Phones_accelerometer.csv)
            String first = null;

            String p = reader.getLine();
            while (p != null) {
                if (first == null && isThereFirst[i]) {
                    first = p;
                    continue;
                } else {
                    strings.add(p);
                }
                p = reader.getLine();
            }

            reader.close();

            //Shuffle everything
            Collections.shuffle(strings);

            //Write lines
            if (isThereFirst[i]) {
                writer.write(first);
            }
            for (String s : strings) {
                writer.write(s);
            }

            writer.flush();
            writer.close();
        }
    }
}
