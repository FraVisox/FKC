# Use of this folder
The input folder is /results/experiments_results/ (csv files created from the results of the experiments).
The output folder is /results/graphs/, where all the png images will be put.

## Use of files
Be careful: since some csv files were updated recently, they may not work as intended.
- all_blobs.py will make lineplot of blobs datasets for window sizes 10000, 20000 and 30000;
- delta_lines.py will make lineplot of PHONE, HIGGS, COVTYPE datasets for window sizes 10000 and varying deltas;
- fairness.py will make barplot of PHONE, HIGGS, COVTYPE datasets for window sizes 10000 with and without fairness constraints;
- merge_csv.py will create merged.csv by adding all the files in /results/experiments_results/CHEN/ and the ones with jones in their name;
- perfect.py will make barplot of a dataset of known radius (/data/randomized/perfect.csv) for the various algorithms;
- rotated.py will make lineplot of PHONE dataset, by first adding dimensions and then rotating it, for the various algorithms;
- wsize_lines.py will make lineplot of PHONE, HIGGS, COVTYPE datasets for varying window sizes;

## Documentation:
- POLARS
https://docs.pola.rs/api/python/stable/reference/api/polars.read_csv.html
- SEABORN
https://seaborn.pydata.org/tutorial/introduction.html