#!/bin/bash

curl -F data=@download/deuidxeur-s1-2019-01-01-2020-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2020-01-01-2021-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2021-01-01-2022-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2022-01-01-2023-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2023-01-01-2024-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2024-01-01-2025-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2025-01-01-2026-01-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"
curl -F data=@download/deuidxeur-s1-2026-01-01-2026-07-01.csv "http://localhost:9000/imp?name=DUKASCOPY_6374_OHLC"

