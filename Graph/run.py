import json
import matplotlib.pyplot as plt
from matplotlib.dates import DateFormatter
from datetime import datetime
from statistics import mean
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

sample_count = 0

def get_data(sample):
    data = sample[2].split("=")[1].split(",")
    avg = mean([int(i) for i in data])
    return avg

def get_date(sample):
    global sample_count

    if sample > '20:10:00' and sample < '21:00:00':
        sample_count += 1

    return datetime.strptime(sample, '%H:%M:%S')

def parse(data, name):
    global sample_count
    # Der det står -1, bytt til 3 hvis du analyserer record_1_D og record_2_D
    # Glemte å konfigurere riktig tid på den ene enheten...
    date = [get_date(i['implicitTS'].split(" ")[-1]) for i in data[0]['samples']]
    data = [get_data(i['sample'].split(", ")) for i in data[0]['samples']]

    fig, ax = plt.subplots()
    plt.plot(date, data)

    ax.xaxis.set_major_formatter(DateFormatter('%H:%M:%S'))
    ax.xaxis_date()
    ax.xaxis.set_tick_params(rotation=45)

    ax.set_xlabel("Time")
    ax.set_ylabel("Respiration value")
    ax.set_title(name)

    plt.show()
    
    print(sample_count)

def read(filename="record_1_B.json"):
    with open(filename) as f:
        json_data = json.load(f)

        parse(json_data, filename)

if __name__ == "__main__":
    if len(sys.argv) == 2:
        read(sys.argv[1])
    else:
        read()
