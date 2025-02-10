package it.unidp.dei;

//Only contains the code to manage the commands passed by the user, and calls the methods of TestUtils or BlobsTestUtils
public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Command not recognised, be sure to put only one type of request");
        } else if (args[0].equalsIgnoreCase("fair")) {
            System.out.println("\n----------------------\nSTART OF TEST OF FAIRNESS\n----------------------\n");
            TestUtils.testPriceOfFairness();
            System.out.println("\n----------------------\nTEST OF FAIRNESS FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("perfect")) {
            System.out.println("\n----------------------\nSTART OF TEST OF PERFECT\n----------------------\n");
            TestUtils.testPerfectDataset();
            System.out.println("\n----------------------\nTEST OF PERFECT FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("rr")) {
            System.out.println("\n----------------------\nSTART OF TEST OF ROTATED DATASETS\n----------------------\n");
            TestUtils.testRotatedPhones();
            System.out.println("\n----------------------\nTEST OF ROTATED DATASETS FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("w")) {
            System.out.println("\n----------------------\nSTART OF TEST OF WSIZE\n----------------------\n");
            TestUtils.testWSize();
            System.out.println("\n----------------------\nWSIZE TEST FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("b")) {
            System.out.println("\n----------------------\nSTART OF TEST OF BETA\n----------------------\n");
            TestUtils.testBeta();
            System.out.println("\n----------------------\nBETA TEST FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("k")) {
            System.out.println("\n----------------------\nSTART OF TEST OF KI\n----------------------\n");
            TestUtils.testKi();
            System.out.println("\n----------------------\nKI TEST FINISHED\n----------------------\n");
        } else if (args[0].equalsIgnoreCase("dd")) {
            System.out.println("\n----------------------\nSTART OF TEST OF BLOBS\n----------------------\n");
            BlobsTestUtils.testBlobs();
            System.out.println("\n----------------------\nBLOBS TEST FINISHED\n----------------------\n");
        } else {
            System.out.println("Command "+args[0]+" not recognized, aborting");
        }
    }
}
