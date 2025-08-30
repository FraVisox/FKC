# Use of this folder
The input folder is /results/experiments_results/ (csv files created from the results of the experiments).

The output folder is /results/graphs/, where all the png images will be put.

Inside of /distances/ there are the files that contain the minimum and maximum distances for every dataset.

Inside of /distribution_of_ki/ there are the files with the distributions of points for every category for every dataset.

## Use of files
Be careful: since some csv files were updated recently, they may not work as intended.
- all_blobs.py will make lineplot of blobs datasets for window sizes 10000, 20000 and 30000;
- cat_lines.py will make lineplot of PHONE dataset for varying number of categories;
- delta_lines.py will make lineplot of PHONE, UBER, BEERS datasets for window sizes 30000, k=30 and varying deltas;
- fairness.py will make barplot of PHONE, HIGGS, COVTYPE datasets for window sizes 10000 with and without fairness constraints;
- k100.py will make lineplot of PHONE, UBER, BEERS datasets for window sizes 30000, k=100 and varying deltas;
- ki_lines.py will make lineplot of PHONE dataset for window sizes 10000 and varying k;
- merge_csv.py will create merged.csv by adding all the files in /results/experiments_results/CHEN/ and the ones with jones in their name;
- perfect.py will make barplot of a dataset of known radius (/data/randomized/perfect.csv) for the various algorithms;
- rotated.py will make lineplot of PHONE dataset, by first adding dimensions and then rotating it, for the various algorithms;
- wsize_lines.py will make lineplot of PHONE, UBER, BEERS datasets for varying window sizes;

## Documentation:
- POLARS
https://docs.pola.rs/api/python/stable/reference/api/polars.read_csv.html
- SEABORN
https://seaborn.pydata.org/tutorial/introduction.html