# SCRIPT FOR READING rotated.csv THAT CONTAINS DATA FOR DATASETS PHONE
# WITH DIFFERENT ROTATIONS

import seaborn as sns
import polars as pl
import matplotlib.pyplot as plt

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
    ).with_columns(
        pl.col("algorithm").str.replace("JONES", "JONESETAL")
    )

    for graph in y_axis:
        # Create larger figure with better proportions
        g = sns.FacetGrid(
            df,
            col="dataset",
            sharex=False,
            sharey=False,
            height=2.5,       # Added height parameter (increased size)
            aspect=1.2,     # Adjusted aspect ratio
            margin_titles=True
        )

        hue_order = ["JONESETAL", "OURS 0.5", "OURSOBLIVIOUS 0.5",
                     "OURSOBLIVIOUS 2.0", "OURS 2.0"]

        g.map_dataframe(
            sns.lineplot,  #barplot or lineplot
            x    = x_axis,   #x axis
            y    = graph, #y axis
            hue  = color, #color
            linewidth=2,    # Slightly reduced from 3 for better proportion
            hue_order = hue_order,
            markers=['^', 'v', 's', 'D', '*'],
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

        # Handle log scale for query graph
        if graph == "query":
            for ax in g.axes.flat:
                ax.set_yscale("log")

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