#!/bin/bash

docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2019-01-01 2020-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2020-01-01 2021-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2021-01-01 2022-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2022-01-01 2023-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2023-01-01 2024-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2024-01-01 2025-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2025-01-01 2026-01-01 s1
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh usa500idxusd 2026-01-01 2026-07-01 s1

