# Where to find original datasets
In order to use the original datasets it is necessary to download them from their sources and put them in /data/originals/:
- HIGGS: https://archive.ics.uci.edu/dataset/280/higgs
- PHONES: https://archive.ics.uci.edu/dataset/344/heterogeneity+activity+recognition
- COVERTYPE: https://archive.ics.uci.edu/dataset/31/covertype
- UBER: https://github.com/fivethirtyeight/uber-tlc-foil-response
- BEERS: https://www.kaggle.com/datasets/thedevastator/1-5-million-beer-reviews-from-beer-advocate

# Where to find randomized datasets
The randomized datasets are in part based on the original datasets by executing one or more algorithms of /src/datasetUtils/
and in part created by executing one algorithm in /src/datasetUtils/
For reference see those files.

# Python files
For preprocessing the dataset, we used:
- merger_uber.py to create a comprehensive uber csv
- beers.py filters and aggregates the dataset BEERS to obtain 27 categories