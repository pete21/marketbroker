
## Build

```bash
docker build -t registry.tradefiapp.com:5443/dukascopy-node:0.1.0 .
```

## Run

```bash
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 npx dukascopy-node -i btcusd -r 2 -p bid -ch true -from 2019-01-01 -to 2020-01-01 -t s1 -f csv -d
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 npx dukascopy-node -i btcusd -r 2 -p ask -ch true -from 2019-01-01 -to 2020-01-01 -t s1 -f csv -d
```

## Run through helper script

```bash
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 ./fetch_merge.sh deuidxeur 2019-01-01 2020-01-01 s1

```

## Exec into container

```bash
docker run -it -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache registry.tradefiapp.com:5443/dukascopy-node:0.1.0 /bin/sh

./fetch_merge.sh deuidxeur 2019-01-01 2020-01-01 s1

npx dukascopy-node -i deuidxeur -r 2 -p bid -ch true -from 2019-01-01 -to 2020-01-01 -t s1 -f csv -d
npx dukascopy-node -i deuidxeur -r 2 -p ask -ch true -from 2019-01-01 -to 2020-01-01 -t s1 -f csv -d
```
