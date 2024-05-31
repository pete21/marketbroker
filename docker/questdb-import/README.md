## CSV Import

```bash
curl -F data=@test_file.csv 'http://localhost:9000/imp?name=ticker_price'
```


```bash

curl -G \
  --data-urlencode "query=SELECT timestamp, tempF FROM weather LIMIT 2;" \
  --data-urlencode "count=true" \
  http://localhost:9000/exec

```

- unix timestamp should be in microsecond format
- URL-encoded: http://localhost:9000/exec?query=CREATE TABLE IF NOT EXISTS ticker_price(timestamp TIMESTAMP,q symbol CAPACITY 2,b float,a float) TIMESTAMP(timestamp) PARTITION BY DAY WAL DEDUP UPSERT KEYS(timestamp)



Documentation: https://questdb.io/docs/guides/import-csv/