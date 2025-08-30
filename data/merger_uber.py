import pandas as pd
import glob

def merge_and_sort_csv(input_pattern, output_file):
    # Find all matching CSV files
    csv_files = glob.glob(input_pattern)

    # Read all files into a single DataFrame
    df_list = [pd.read_csv(file) for file in csv_files]

    # Concatenate all DataFrames
    merged_df = pd.concat(df_list, ignore_index=True)

    # Sort by Date/Time (string to datetime for correct ordering)
    merged_df['Date/Time'] = pd.to_datetime(merged_df['Date/Time'])
    merged_df = merged_df.sort_values(by='Date/Time')

    # Write to CSV without header and index
    merged_df.to_csv(output_file, index=False, header=False)

if __name__ == "__main__":
    # Example: if your files are named "uber-1.csv", "uber-2.csv", etc.
    merge_and_sort_csv("*.csv", "uber.csv")
