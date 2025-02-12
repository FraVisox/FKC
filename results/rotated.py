# SCRIPT FOR READING rotated.csv THAT CONTAINS DATA FOR DATASETS PHONE
# WITH DIFFERENT ROTATIONS

import seaborn as sns
import polars as pl
import matplotlib.pyplot as plt

# Parameters to change
first = False

# Parameters
x_axis = "dimensions"
y_axis = ["update", "query", "memory", "ratio", "radius"]
color = "algorithm"

# File to read from
file_name = "experiments_results/rotated.csv"
output_file = "graphs/rotated"

COLORS = sns.color_palette()
PALETTE = {
    "JONES": COLORS[1],
    "OURSOBLIVIOUS": COLORS[2],
    "OURS": COLORS[3],
    "CHENETAL": COLORS[4],
}

def read_and_plot(output_file_path):
    df = pl.read_csv(source=file_name, separator=";")
    df = df.with_columns(
        pl.lit("rotated").alias("dataset")
    ).filter(
        pl.col("algorithm").is_in(["JONES","CAPPDELTA05", "CAPPDELTA20", "PELLCAPPDELTA05", "PELLCAPPDELTA20"])
    )
    df = df.with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA05", "OURSOBLIVIOUS 0.5"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA05", "OURS 0.5")
    ).with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA10", "OURSOBLIVIOUS 1.0"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA10", "OURS 1.0")
    ).with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA15", "OURSOBLIVIOUS 1.5"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA15", "OURS 1.5")
    ).with_columns(
        pl.col("algorithm").str.replace("PELLCAPPDELTA20", "OURSOBLIVIOUS 2.0"),
    ).with_columns(
        pl.col("algorithm").str.replace("CAPPDELTA20", "OURS 2.0")
    ).with_columns(
        pl.col("algorithm").str.replace("CHEN", "CHENETAL")
    )
    for graph in y_axis:
        g = sns.FacetGrid(df, col="dataset", sharex=False, sharey=False, aspect=1.5)
        hue_order = ["JONES", "OURS 0.5", "OURSOBLIVIOUS 0.5", 
                     "OURSOBLIVIOUS 2.0", "OURS 2.0"]
        g.map_dataframe(
            sns.lineplot,  #barplot or lineplot
            x    = x_axis,   #x axis
            y    = graph, #y axis
            hue  = color, #color
            #marker="o",
            linewidth=3,
            hue_order = hue_order,
            markers=['^', 'v', 's', 'D', '*'],
            size="algorithm",
            style="algorithm",
            legend="brief",
            size_order=hue_order,
            markersize=8,
            dashes=False
            )
        g.add_legend()
        #plt.gcf().set_size_inches(8, 5)
        if graph == "query":
            plt.yscale("log")
        plt.savefig(output_file_path+"_"+graph+".png", bbox_inches='tight')

# USE
read_and_plot(output_file)