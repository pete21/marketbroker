#!/bin/bash
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2020-01-01 2020-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2021-01-01 2021-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2022-01-01 2022-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2023-01-01 2023-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2024-01-01 2024-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2025-01-01 2025-12-31 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh vxxusdusd 2026-01-01 2026-06-30 s1

