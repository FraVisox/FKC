import numpy as np
import seaborn as sns
import polars as pl
import matplotlib.pyplot as plt

# Parameters to change
replace_commas = False
graph_name = "deltas_lines"
input_file_name = "wsize"
y_axis = ["update", "query", "memory", "ratio"]
color = "algorithm"

# File to read from
datasets = ["phones", "higgs", "covtype"]
file_names = [f"{input_file_name}_jones_{dataset}" for dataset in datasets]
output_file = f"graphs/{graph_name}"

COLORS = sns.color_palette()
PALETTE = {
    "CHENETAL": COLORS[0],
    "JONES": COLORS[1],
    "OURSOBLIVIOUS": COLORS[2],
    "OURS": COLORS[3],
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
        pl.col("algorithm").str.replace("JONES", "JONES")
    )
    df = df.filter(pl.col("wsize").is_in([10000]))
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
    baseline = data[data["algorithm"].isin(["JONES", "CHENETAL"])]
    for _, row in baseline.iterrows():
        algo = row["algorithm"]
        linestyle = ":" if algo == "Jones" else "--"
        ax.axhline(row[y],
                  color=PALETTE[algo],
                  linestyle=linestyle,
                  label=f"{algo}")
        
    # Handle non-baseline algorithms
    non_baseline = data[~data["algorithm"].isin(["JONES", "CHENETAL"])]
    markers = ['^', 'x']
    i = 0
    for algo in ["OURS", "OURSOBLIVIOUS"]:
        algo_data = non_baseline[non_baseline["algorithm"] == algo]
        if not algo_data.empty:
            ax.plot(algo_data[x], algo_data[y],
                   marker=markers[i], 
                   linestyle='-',
                   color=PALETTE[algo],
                   label=algo)
            i += 1

def load(file, basedir="experiments_results/"):
    input_file = f"{basedir}{file}.csv"
    print(f"Loading file: {input_file}")

    df = pl.read_csv(source=input_file, separator=";", infer_schema_length=10000, truncate_ragged_lines=True)
    df = df.with_columns(pl.lit(file.split("_")[-1].upper()).alias("dataset"))
    if "wsize" not in df.columns:
        df = df.with_columns(pl.lit(10000, pl.Int64).alias("wsize"))
    if "type" in df.columns:
        df = df.filter(pl.col("type") == "Rand")
    df = df.select(
        "wsize", "algorithm", "update", "query", "radius", "ratio", "memory", "dataset"
    )
    return filter(df)

def read_and_plot_bar(output_file_path):
    """
    Reads data from input files, performs filtering, and generates bar plots.
    """
    dataframe = []
    for dataset in datasets:
        file = "wsize_jones_" + dataset
        df = load(file)
        dataframe.append(df)
    dat = pl.concat(dataframe)
    
    # Convert Polars DataFrame to Pandas for seaborn compatibility
    dat = dat.to_pandas()

    for graph in y_axis:
        g = sns.FacetGrid(
            dat,
            col="dataset",
            col_wrap=3,
            sharex=False,
            sharey=graph == "query",
            height=2,
            aspect=1.8,
        )
        
        g.map_dataframe(
            plot_lines,
            "delta",
            graph
        )
        
        g.set_xlabels(r"$\delta$", usetex=True)
        
        # Get the last subplot and add the legend there
        last_ax = g.axes.flat[-1]
        handles, labels = last_ax.get_legend_handles_labels()
        last_ax.legend(handles, labels, 
                      bbox_to_anchor=(1.05, 1),
                      loc='upper left',
                      borderaxespad=0.)

        if graph == "query":
            for ax in g.axes.flat:
                ax.set_yscale("log")
            
        plt.tight_layout()
        plt.savefig(output_file_path + "_" + graph + ".png", 
                   dpi=300,
                   bbox_inches='tight')

# USE
read_and_plot_bar(output_file)