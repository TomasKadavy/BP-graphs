import pandas
import pm4py
import sys


def create_svg(input_file, output_file):
    file = pandas.read_csv(input_file, sep=",")
    sorted_file = file.sort_values(by=['timestamp_str'], ascending=True)
    sorted_file.to_csv(input_file, index=False)
    file = pm4py.format_dataframe(sorted_file, case_id="sandbox_id", activity_key="cmd_name", timestamp_key="timestamp_str")
    dfg = pm4py.discover_heuristics_net(file)
    pm4py.save_vis_heuristics_net(dfg, output_file)


create_svg(sys.argv[1], sys.argv[2])
