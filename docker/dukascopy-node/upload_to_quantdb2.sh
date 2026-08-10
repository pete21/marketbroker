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
./populate_cache2.sh "${symbol^^}" "$last_date" "$today"

# Upload data to quantdb
# curl -F data=@<file>.csv "http://localhost:9000/imp?name=${table_base}"

curl -F schema='[{"name":"time", "type": "TIMESTAMP", "pattern": "yyyy-MM-dd HH:mm:ssZ"}]' -F data=@download2/${symbol}-${last_date}-${today}.csv "http://localhost:9000/imp?name=${table_base}_CSV"
echo "Uploaded CSV file to table ${table_base}_CSV"


# Migrate data to ${table_base}_1S
# INSERT INTO ${table_base}_1S SELECT cast(timestamp AS TIMESTAMP), first((b+a)/2), max((b+a)/2), min((b+a)/2), last((b+a)/2) FROM ${table_base}


curl -G http://localhost:9000/exec \
  --data-urlencode "query=INSERT INTO ${table_base}_1S SELECT timestamp, open, high, low, close FROM ${table_base}_CSV"     # where cast(timestamp AS TIMESTAMP) >= '${last_date}'
echo "Migrated data to table ${table_base}_1S"


# Truncate ${table_base}_CSV
curl -G http://localhost:9000/exec \
  --data-urlencode "query=TRUNCATE TABLE ${table_base}_CSV"
echo "Truncated table ${table_base}_CSV"
