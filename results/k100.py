import numpy as np
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
replace_commas = True
graph_name = "deltas_lines_k100"
input_file_name = "wsize"
y_axis = ["update", "query", "memory", "ratio"]
color = "algorithm"

# File to read from
datasets = ["phones", "higgs", "covtype"]
file_names = ["jab"]#f"{input_file_name}_jones_{dataset}" for dataset in datasets]
output_file = f"graphs/{graph_name}"

COLORS = sns.color_palette()
PALETTE = {
    "CHENETAL": COLORS[6],
    "JONESETAL": COLORS[0],
    "OURSOBLIVIOUS": COLORS[2],
    "OURS": COLORS[1],
}

def filter(df):
    """
    Filters the given DataFrame `df` depending on the type of graph.
    """
    df = df.filter(
        pl.col("dataset") != "RANDOM",
        pl.col("dataset") != "NORMALIZED",
        )
    df = df.with_columns(
        pl.col("update").str.replace(",", ".").cast(pl.Float64).alias("update"),
        pl.col("query").str.replace(",", ".").cast(pl.Float64).alias("query"),
        pl.col("radius").str.replace(",", ".").cast(pl.Float64).alias("radius"),
        pl.col("memory").str.replace(",", ".").cast(pl.Float64).alias("memory"),
        pl.col("ratio").str.replace(",", ".").cast(pl.Float64).alias("ratio"),
    )
    df = df.with_columns(
        pl.col("algorithm").str.extract(r"DELTA(\d+)").cast(pl.Float64).alias("delta") / 10
    )
    df = df.with_columns(
        pl.col("algorithm").str.replace(r"PELLCAPPDELTA(\d+)", "OURSOBLIVIOUS"),
    ).with_columns(
        pl.col("algorithm").str.replace(r"CAPPDELTA(\d+)", "OURS")
    ).filter(
        pl.col("algorithm").is_in(["JONES", "CHEN", "OURS", "OURSOBLIVIOUS"])
    ).with_columns(
        pl.col("algorithm").str.replace("CHEN", "CHENETAL")
    ).with_columns(
        pl.col("algorithm").str.replace("JONES", "JONESETAL")
    )
    #df = df.filter(pl.col("wsize").is_in([10000]))
    df = df.with_columns(
        (pl.col("update") / 1e6).alias("update"),
        (pl.col("query") / 1e6).alias("query")
    )
    return df

def plot_lines(x, y, data=None, **kwargs):
    """
    Custom plotting function that handles both baseline and non-baseline algorithms.
    """
    ax = plt.gca()

    # Handle baseline algorithms
    baseline = data[data["algorithm"].isin(["JONESETAL", "CHENETAL"])]
    for _, row in baseline.iterrows():
        algo = row["algorithm"]
        linestyle = ":" if algo == "JONESETAL" else "--"
        ax.axhline(row[y],
                   color=PALETTE[algo],
                   linestyle=linestyle,
                   label=f"{algo}",
                   linewidth=1.5)  # Slightly thicker lines

    # Handle non-baseline algorithms
    non_baseline = data[~data["algorithm"].isin(["JONESETAL", "CHENETAL"])]
    markers = ['^', 'x']
    i = 0
    for algo in ["OURS", "OURSOBLIVIOUS"]:
        algo_data = non_baseline[non_baseline["algorithm"] == algo]
        if not algo_data.empty:
            ax.plot(algo_data[x], algo_data[y],
                    marker=markers[i],
                    linestyle='-',
                    color=PALETTE[algo],
                    label=algo,
                    linewidth=1.5,  # Slightly thicker lines
                    markersize=6)   # Slightly larger markers
            i += 1

def load(file, basedir="experiments_results/"):
    input_file = f"{basedir}{file}.csv"
    print(f"Loading file: {input_file}")

    df = pl.read_csv(source=input_file, separator=";", infer_schema_length=10000, truncate_ragged_lines=True)
    df = df.with_columns(pl.lit(file.split("_")[-1].upper()).alias("dataset"))
    if "wsize" not in df.columns:
        df = df.with_columns(pl.lit(10000, pl.Int64).alias("wsize"))
    df = df.select(
        "wsize", "algorithm", "update", "query", "radius", "ratio", "memory", "dataset"
    )
    return filter(df)

def read_and_plot_bar(output_file_path):
    """
    Reads data from input files, performs filtering, and generates bar plots.
    """
    dataframe = []
    #for dataset in datasets:
        #file = "wsize_jones_" + dataset
    df = load("k100_phones")
    dataframe.append(df)
    df = load("k100_uber")
    dataframe.append(df)
    df = load("k100_beers")
    dataframe.append(df)
    dat = pl.concat(dataframe)

    # Convert Polars DataFrame to Pandas for seaborn compatibility
    dat = dat.to_pandas()
    
    for graph in y_axis:
        # Create larger figure with better proportions
        g = sns.FacetGrid(
            dat,
            col="dataset",
            col_wrap=3,
            sharex=False,
            sharey=graph == "query",
            height=2.5,      # TODO: change
            aspect=1.7,    # TODO: change
            margin_titles=True
        )

        g.map_dataframe(
            plot_lines,
            "delta",
            graph
        )

        g.set_xlabels(r"$\delta$", usetex=True)

        # Get the last subplot and add the legend there with smaller font
        last_ax = g.axes.flat[-1]
        handles, labels = last_ax.get_legend_handles_labels()
        last_ax.legend(handles, labels,
                       bbox_to_anchor=(1.05, 1),
                       loc='upper left',
                       borderaxespad=0.,
                       fontsize=9,        # Smaller legend font
                       frameon=True,
                       fancybox=True,
                       shadow=False)

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
read_and_plot_bar(output_file)