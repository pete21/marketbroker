#!/bin/bash

#=======================================================================
# Usage: ./populate_cache.sh <instrument> <start_date> <end_date>
# Example: ./populate_cache.sh deuidxeur 2026-01-01 2026-06-30
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
    month_zero_based=$(( 10#${month}-1))
    if [ ${month_zero_based} -lt 10 ]; then
        month_zero_based="0${month_zero_based}"
    fi
    day=$(date -d "${start}" +%d)
    hour=$(date -d "${start}" +%H)

    formatted_start=${year}/${month_zero_based}/${day}/${hour}
    formatted_start_file=${year}-${month_zero_based}-${day}-${hour}


    is_weekend=$(date -d "${start}" +%u)
    # if [ ${is_weekend} -eq 6 ] || [ ${is_weekend} -eq 7 ]; then
    if [ ${is_weekend} -eq 6 ]; then
        echo "Skipping Saturday: ${year}/${month}/${day}/${hour}h"
        touch ./cache/${instrument}-${formatted_start_file}h_ticks.bi5
        start=$(date -d "${start} ${increment}")
        continue
    fi

    if [ ${is_weekend} -eq 7 ] && [ ${hour} -lt 21 ]; then
        echo "Skipping Sunday: ${year}/${month}/${day}/${hour}h"
        touch ./cache/${instrument}-${formatted_start_file}h_ticks.bi5
        start=$(date -d "${start} ${increment}")
        continue
    fi

    # echo Downloading: https://datafeed.dukascopy.com/datafeed/${instrument}/${formatted_start}h_ticks.bi5
    # echo Cached file: ./cache/${instrument}-${formatted_start_file}h_ticks.bi5

    # Download the data
    if [ ! -s ./cache/${instrument}-${formatted_start_file}h_ticks.bi5 ]; then
        wget --retry-on-http-error=500,503,504 --waitretry=2 -t 2 https://datafeed.dukascopy.com/datafeed/${instrument}/${formatted_start}h_ticks.bi5 -O ./cache/${instrument}-${formatted_start_file}h_ticks.bi5
        sleep .5
    else
        echo "File exists: ./cache/${instrument}-${formatted_start_file}h_ticks.bi5"
    fi

    # Increment the value. This changes the value of `$start` every loop
    # with the next date.

    start=$(date -d "${start} ${increment}")

done
