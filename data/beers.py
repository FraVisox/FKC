# /// script
# requires-python = ">=3.11"
# dependencies = [
#     "polars",
# ]
# ///
import polars as pl


def aggregate_beers(threshold=10000):
    data = pl.read_csv("beer_reviews.csv.gz").with_columns(
        pl.col("beer_style")
        .str.replace(r".*IPA.*", "IPA")
        .str.replace(r".*APA.*", "APA")
        .str.replace(r".*[Ss]tout.*", "Stout")
        .str.replace(r".*[Ll]ager.*", "Lager")
        .str.replace(r".*[Aa]le.*", "Ale")
        # .str.replace(r".*(Pale |Dark |Strong |Red |Black |Blonde )[Aa]le.*", "${1}Ale")
        .str.replace(r".*[Pp]ilsener.*", "Pilsener")
    )

    counts = data["beer_style"].value_counts().sort("count")
    infrequent = counts.filter(pl.col("count") < threshold)["beer_style"]

    data = (
        data
        .with_columns(pl.col("beer_style").replace(infrequent, "Other"))
        .sort("review_time")
    )
    counts = (
        data["beer_style"]
        .value_counts()
        .sort("count")
        .select("beer_style")
        .with_row_count("beer_style_id")
    )

    data = data.join(counts, on="beer_style")

    print(data["beer_style"].value_counts())

    data.write_csv(f"beers_filtered_{threshold}.csv")


aggregate_beers(10000)
aggregate_beers(50000)
