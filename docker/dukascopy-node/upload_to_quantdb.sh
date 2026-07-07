#!/bin/bash

instrument_id="${1:?Usage: $0 <instrument_id> <symbol>}"
symbol="${2:?Usage: $0 <instrument_id> <symbol>}"

table_base="DUKASCOPY_${instrument_id}_OHLC"

# Query last available date +1 day from quantdb
last_date=$(curl -s -G http://localhost:9000/exec --data-urlencode "query=select to_str(max(dateadd('d',1,timestamp)), 'yyyy-MM-dd') as timestamp FROM ${table_base}_1S" | jq -r '.dataset[0][0]')
echo "Last date +1 day: $last_date"

today=$(date +%Y-%m-%d)
echo "Today: $today"

if [ "$last_date" == "$today" ]; then
    echo "Last date is equal to today, skipping..."
    exit 0
fi

# Populate cache
./populate_cache.sh "${symbol^^}" "$last_date" "$today"

# Download data from dukascopy
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh "$symbol" "$last_date" "$today" s1
# output file: <symbol>-s1-<last_date>-<today>.csv
echo "Output file: download/${symbol}-s1-$last_date-$today.csv"

# Upload data to quantdb
# curl -F data=@<file>.csv "http://localhost:9000/imp?name=${table_base}"

curl -F data=@download/${symbol}-s1-$last_date-$today.csv "http://localhost:9000/imp?name=${table_base}"
echo "Uploaded data to quantdb table ${table_base}"


# Migrate data to ${table_base}_1S
# INSERT INTO ${table_base}_1S SELECT cast(timestamp AS TIMESTAMP), first((b+a)/2), max((b+a)/2), min((b+a)/2), last((b+a)/2) FROM ${table_base}

curl -G http://localhost:9000/exec \
  --data-urlencode "query=INSERT INTO ${table_base}_1S SELECT cast(timestamp AS TIMESTAMP) date, first((b+a)/2), max((b+a)/2), min((b+a)/2), last((b+a)/2) FROM ${table_base}"     # where cast(timestamp AS TIMESTAMP) >= '${last_date}'
echo "Migrated data to ${table_base}_1S"


# Truncate ${table_base}
curl -G http://localhost:9000/exec \
  --data-urlencode "query=TRUNCATE TABLE ${table_base}"
echo "Truncated ${table_base}"
