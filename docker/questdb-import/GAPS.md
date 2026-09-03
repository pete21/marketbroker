```SQL

CREATE TABLE 'GAPS_6374_OHLC_1M' ( 
	timestamp TIMESTAMP,
	open FLOAT,
	high FLOAT,
	low FLOAT,
	close FLOAT,
  vol FLOAT
) timestamp(timestamp) PARTITION BY DAY WAL
DEDUP UPSERT KEYS(timestamp);

CREATE TABLE 'GAPS_16917_OHLC_1M' ( 
	timestamp TIMESTAMP,
	open FLOAT,
	high FLOAT,
	low FLOAT,
	close FLOAT,
  vol FLOAT
) timestamp(timestamp) PARTITION BY DAY WAL
DEDUP UPSERT KEYS(timestamp);

CREATE TABLE 'GAPS_872703_OHLC_1M' ( 
	timestamp TIMESTAMP,
	open FLOAT,
	high FLOAT,
	low FLOAT,
	close FLOAT,
  vol FLOAT
) timestamp(timestamp) PARTITION BY DAY WAL
DEDUP UPSERT KEYS(timestamp);




```