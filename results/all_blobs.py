# SCRIPT FOR READING all_blobs.csv THAT CONTAINS DATA FOR DATASETS BLOBS 2-10
# FOR WINDOW SIZES 10000, 20000 AND 30000

import seaborn as sns
import polars as pl
import matplotlib.pyplot as plt
import numpy as np

# Set matplotlib parameters for better text sizing
plt.rcParams.update({
    'font.size': 10,          # Base font size
    'axes.labelsize': 10,     # Axis label size
    'axes.titlesize': 12,     # Title size
    'xtick.labelsize': 9,     # X-axis tick label size
    'ytick.labelsize': 9,     # Y-axis tick label size
    'legend.fontsize': 9,     # Legend font size
    'figure.titlesize': 14    # Figure title size
})

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
    ).with_columns(
        pl.col("algorithm").str.replace("JONES", "JONESETAL")
    )

    for graph in y_axis:
        # Create larger figure with better proportions
        g = sns.FacetGrid(
            df,
            col="dataset",
            sharex=False,
            sharey=True,
            height=2.5,       # Added height parameter (increased size)
            aspect=1.2,     # Adjusted aspect ratio
            margin_titles=True
        )

        hue_order = ["JONESETAL", "OURS 0.5","OURSOBLIVIOUS 0.5",# "OURSOBLIVIOUS 1.0", "OURS 1.0", "OURSOBLIVIOUS 1.5", "OURS 1.5",
                     "OURS 2.0", "OURSOBLIVIOUS 2.0"]

        g.map_dataframe(
            sns.lineplot,  #barplot or lineplot
            x    = x_axis,   #x axis
            y    = graph, #y axis
            hue  = color, #color
            linewidth=2,    # Slightly reduced from 3 for better proportion
            hue_order = hue_order,
            markers=['^', 'v', '*', 's', 'D'],
            size="algorithm",
            style="algorithm",
            legend="brief",
            size_order=hue_order,
            markersize=8,   # Slightly reduced from 8 for better proportion
            dashes=False
        )

        # Customize legend with smaller font
        g.add_legend(fontsize=9,
                     frameon=True,
                     fancybox=True,
                     shadow=False,
                     bbox_to_anchor=(1.05, 1),
                     loc='upper left')

        # Adjust spacing and layout
        plt.tight_layout()

        # Save with optimized settings for crisp output
        plt.savefig(output_file_path + "_" + graph + ".png",
                    dpi=150,              # Reduced DPI since figure is now larger
                    bbox_inches='tight',
                    facecolor='white',
                    edgecolor='none',
                    pad_inches=0.1)

        plt.close()  # Close the figure to free memory

# USE
read_and_plot(output_file)