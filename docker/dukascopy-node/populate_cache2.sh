#!/bin/bash

#=======================================================================
# Usage: ./populate_cache.sh <instrument> <start_date> <end_date>
# Examples:
# ./populate_cache2.sh DEU.IDX-EUR 2026-07-01 2026-08-01
# ./populate_cache2.sh USATECH.IDX-USD 2026-07-01 2026-08-01
# ./populate_cache2.sh USA500.IDX-USD 2026-07-01 2026-08-01
# 
#=======================================================================


# Use format "HH:MM YYYY-MM-DD"
start="00:00 $2"
end="00:00 $3"
increment="+1 hours"
instrument=$1
#=======================================================================

# Convert start and end time to full Bash datetime format
# For example, it converts "00:00 2021-04-01" to "Thu Apr 1 00:00:00 GMT 2021"
start=$(date -d "${start}")
end=$(date -d "${end}")
printf "Start: %s\n" "${start}"
printf "End: %s\n" "${end}"

# echo $(date -d "${start}" +%F)
# echo $(date -d "${end}" +%F)

# Output csv file name:
output_csv_file="${instrument}-$(date -d "${start}" +%F)-$(date -d "${end}" +%F).csv"
echo "Output csv file: ${output_csv_file}"
rm ./download2/${output_csv_file}
touch ./download2/${output_csv_file}

# The below while statement will loop over each date between the start and
# end time. Each loop will increment the date by "+1 hours" (defined above).

# NOTE: The +%s in the first line converts the date to "seconds since 
# EPOC" which makes the comparison between start and end time possible.
# See bottom of page here: https://phoenixnap.com/kb/linux-date-command
while (( $(date -d "${start}" +%s) < $(date -d "${end}" +%s) )); do
    # echo      #< empty echo statement prints a blank line
    # echo Current Loop Date: ${start}
   
    # DO SOME STUFF WITH THE DATE

    # Format the start date for the URL
    # NOTE: The month is 0-indexed in the URL and file name, so we need to subtract 1
    year=$(date -d "${start}" +%Y)
    month=$(date -d "${start}" +%m)
    day=$(date -d "${start}" +%d)
    hour=$(date -d "${start}" +%H)

    formatted_start=${year}/$((10#$month))/$((10#$day))/$((10#$hour))
    formatted_start_file=${year}-${month}-${day}-${hour}


    is_weekend=$(date -d "${start}" +%u)
    # if [ ${is_weekend} -eq 6 ] || [ ${is_weekend} -eq 7 ]; then
    if [ ${is_weekend} -eq 6 ]; then
        echo "Skipping Saturday: ${year}/${month}/${day}/${hour}"
        # touch ./cache2/${instrument}-${formatted_start_file}h_ticks.json
        start=$(date -d "${start} ${increment}")
        continue
    fi

    if [ ${is_weekend} -eq 7 ] && [ ${hour} -lt 21 ]; then
        echo "Skipping Sunday: ${year}/${month}/${day}/${hour}"
        # touch ./cache2/${instrument}-${formatted_start_file}h_ticks.json
        start=$(date -d "${start} ${increment}")
        continue
    fi

    # echo Downloading: https://jetta.dukascopy.com/v1/ticks/DEU.IDX-EUR/2026/7/16/23
    # echo Cached file: ./cache2/${instrument}-${formatted_start_file}.json

    # Download the data
    if [ ! -s ./cache2/${instrument}-${formatted_start_file}h_ticks.json ]; then
        wget --retry-on-http-error=500,503,504 --waitretry=2 -t 2 --header="Accept-Encoding: gzip, deflate, br" https://jetta.dukascopy.com/v1/ticks/${instrument}/${formatted_start} -O ./cache2/${instrument}-${formatted_start_file}h_ticks.json -U "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        sleep 1
    else
        echo "File exists: ./cache2/${instrument}-${formatted_start_file}h_ticks.json"
    fi

    # Convert to csv
    if [ -s ./cache2/${instrument}-${formatted_start_file}h_ticks.json ]; then
        python dukascopy_to_ohlc.py ./cache2/${instrument}-${formatted_start_file}h_ticks.json ./download2/${output_csv_file} --bar-size 1s
    else
        echo "Skipping conversion to CSV because file does not exist or is zero size: ./cache2/${instrument}-${formatted_start_file}h_ticks.json"
    fi

    # Increment the value. This changes the value of `$start` every loop
    # with the next date.

    start=$(date -d "${start} ${increment}")

done
