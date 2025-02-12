# K-CENTER CLUSTERING WITH PARTITION MATROID (FAIR K-CENTER CLUSTERING)

## FAIR K-CENTER CLUSTERING
The problem addressed is the Fair K-Center Clustering, a problem of K-Center Clustering where the set of centers calculated by the algorithm should 
be an independent set for a given partition matroid. This problem is addressed on the sliding window model.

## Execution
To execute the program:
1. Install the maven project: the output will be a file FKC_sliding_window-1.0-jar-with-dependencies.jar in the /target/ folder;
2. Make sure to put the jar file in the same directory of a /data/ directory and an /out/ directory: the first
   one will be the one the datasets are (randomized and original, as explained in the section Data), and the other one
   will be where the output files will be put;
3. Execute 
```
java -jar FKC_sliding_window-1.0-jar-with-dependencies.jar 
```
with one of the following options
   - fair to test the price of fairness
   - perfect to test the algorithms on the dataset of known radius
   - rr to test the algorithms on the datasets obtained by adding features to PHONE and then rotating it
   - b to test algorithms when beta changes (not included in the paper as not interesting)
   - w to test algorithms when the window size changes
   - dd to test algorithms when the number of features changes
   - k to test algorithms when K changes (not included in the paper as not interesting)

## Data
In order to use the original datasets it is necessary to download them from their sources and put them in /data/originals/:
- HIGGS: https://archive.ics.uci.edu/dataset/280/higgs
- PHONES: https://archive.ics.uci.edu/dataset/344/heterogeneity+activity+recognition
- COVERTYPE: https://archive.ics.uci.edu/dataset/31/covertype

To create all the other randomized datasets, you could run RandomizeDataset.
All the files used for our tests can be found at: https://drive.google.com/drive/folders/1YraBr_UZhe9sNAXeCGSiTX9hPQdQDIWW?usp=drive_link

These can be downloaded and put in folders to have the following structure:
- /data/originals will contain all the datasets as they can be found online;
- /data/randomized will contain all the datasets generated (BLOBS) and the other datasets, except for
  HIGGS, but with lines swapped randomly.

## Results
Results are produced in the /out/ folder, that needs to be in the same directory as the jar file.
We analyzed the results of some tests, calculating the arithmetic mean and putting them as tidy data in the /results/ folder.
In that folder can be found:
- /results/distances/ contains the distances calculated with the class datasetUtils.CalculateMinMaxDist for all the datasets, both originals and randomized
- /results/distributions_of_ki/ contains all the distributions of ki calculated with the class datasetUtils.FindNumberOfK for all the datasets, both originals and randomized
- /results/experiments_results/ contains all the results of the experiments as tidy data, as explained in /results/use.md
- /results/graphs/ contains the graphs created from the tidy data
- all the algorithms (*.py) used to create the graphs, whose use is explained in /results/use.md

## Organization of code
The code for creating the datasets and executing the algorithms is entirely in the /src/ folder, and is organized as follows:
- /src/datasetUtils/ contains python and Java files to create datasets (even datasets not included in the paper results) and find the distribution of ki and the maximum and minimu distances
- /src/it/unidp/dei/ contains all the Java code used to read the datasets (/datasetReaders/), execute the tests (Main.java, TestUtils.java and BlobsTestUtils.java) and the folders containing the algorithms:
  - /OURS/ contains the algorithms described in our paper
  - /CHENETAL/ contains the algorithms of Chen et al. (for reference see the paper)
  - /JONES/ contains the algorithms of Jones et al. (for reference see the paper)