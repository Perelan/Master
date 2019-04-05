import json
import matplotlib.pyplot as plt
from datetime import *
from dateutil.parser import parse
from matplotlib.dates import DateFormatter
from datetime import datetime
import sys 

import seaborn

plt.style.use('seaborn')

## Created by: Jagat Deep Singh
## Sample structure for Flow data in CESAR:
# [
#   {
#       "record": {  ... (not so important) },
#       "samples":
#           [
#               "sample": {
#                   'explicitTS': 'Apr 3, 2019 18:03:22', 
#                   'id': 13127, 
#                   'implicitTS': 'Apr 3, 2019 20:03:22', 
#                   'recordId': 24, 
#                   'sample': 'Time=60536ms, deltaT=100, data=1554,1554,1565,1570,1580,1578,1560'
#               }
#          ]
#   } 
# ]

def get_data(sample):
    data = sample[2].split("=")[1].split(",")
    
    avg = 0

    for i in data:
        avg += int(i)

    return avg // len(data)

time_base = 0
def get_time(sample):
    global time_base
    time = int(sample[0].split("=")[1][:-2])
    
    if time < time_base:
        time_base += time
    else:
        time_base = time

    return time_base

def get_date(sample):
    return datetime.strptime(sample, '%H:%M:%S')

def read(filename="record_1_B.json"):
    with open(filename) as f:
        json_data = json.load(f)

        parse(json_data)

def parse(data):
    date = [get_date(i['implicitTS'].split(" ")[-1]) for i in data[0]['samples']]
    #time = [get_time(i['sample'].split(", ")) for i in data[0]['samples']]
    data = [get_data(i['sample'].split(", ")) for i in data[0]['samples']]

    fig, ax = plt.subplots()
    plt.plot(date, data)

    ax.xaxis.set_major_formatter(DateFormatter('%H:%M:%S'))
    ax.xaxis_date()
    ax.xaxis.set_tick_params(rotation=45)
    #ticks = [date[idx] for idx in range(0, len(date), 500)]
    #plt.xticks(list(range(0, len(date), 500)), ticks)

    plt.show();


if __name__ == "__main__":
    if len(sys.argv) == 2:
        read(sys.argv[1])
    else:
        read()