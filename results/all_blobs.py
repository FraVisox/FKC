# SCRIPT FOR READING all_blobs.csv THAT CONTAINS DATA FOR DATASETS BLOBS 2-10
# FOR WINDOW SIZES 10000, 20000 AND 30000


import seaborn as sns
import polars as pl
import matplotlib.pyplot as plt
import numpy as np


#File to read from
first = True
file_name = "experiments_results/merged.csv"
output_file = "graphs/dimensionality"

#Parameters
x_axis = "dimensions"
y_axis = ["update", "query", "radius", "ratio", "memory"]
color = 'algorithm'

def read_and_plot(output_file_path):
    df = pl.read_csv(source=file_name, separator=";")
    df = df.filter(
        pl.col("algorithm").is_in(["JONES","CAPPDELTA05", "CAPPDELTA20", "PELLCAPPDELTA05", "PELLCAPPDELTA20"])
    ).filter(
        pl.col("wsize").is_in([10000])
    ).filter(
        pl.col("dataset").is_in(["blobs"])
    )
    df = df.with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA05", "OURSOBLIVIOUS 0.5"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA05", "OURS 0.5")
    ).with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA20", "OURSOBLIVIOUS 2.0"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA20", "OURS 2.0")
    )
    for graph in y_axis:
        g = sns.FacetGrid(df, col="dataset", sharex=False, sharey=True, aspect=1.5)
        hue_order = ["JONES", "OURS 0.5","OURSOBLIVIOUS 0.5",# "OURSOBLIVIOUS 1.0", "OURS 1.0", "OURSOBLIVIOUS 1.5", "OURS 1.5", 
                    "OURS 2.0", "OURSOBLIVIOUS 2.0"]
        g.map_dataframe(
            sns.lineplot,  #barplot or lineplot
            x    = x_axis,   #x axis
            y    = graph, #y axis
            hue  = color, #color
            #marker="o",
            linewidth=3,
            hue_order = hue_order,
            markers=['^', 'v', '*', 's', 'D'],
            size="algorithm",
            style="algorithm",
            legend="brief",
            size_order=hue_order,
            markersize=8,
            dashes=False
            )
        g.add_legend()
        #plt.gcf().set_size_inches(8, 5)
        plt.savefig(output_file_path+"_"+graph+".png", bbox_inches='tight')

# USE
read_and_plot(output_file)