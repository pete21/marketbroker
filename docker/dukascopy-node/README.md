
## Build

```bash
docker build -t dukascopy-node .
```

## Run

```bash
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache dukascopy-node npx dukascopy-node -i btcusd -p bid -ch true -from 2019-01-13 -to 2019-01-14 -t s1 -f csv
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache dukascopy-node npx dukascopy-node -i btcusd -p ask -ch true -from 2019-01-13 -to 2019-01-14 -t s1 -f csv
```

## Run through helper script

```bash
docker run -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache dukascopy-node ./fetch_merge.sh deuidxeur 2018-01-01 2018-12-31 s1

```

## Exec into container

```bash
docker run -it -v $(pwd)/download/:/home/pn/app/download -v $(pwd)/cache:/home/pn/app/.dukascopy-cache dukascopy-node /bin/sh

./fetch_merge.sh deuidxeur 2018-01-01 2018-12-31 s1
```
