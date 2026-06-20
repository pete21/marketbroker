# marketbroker



### Docker build

```bash
docker build -f ./Dockerfile -t registry.tradefiapp.com:5443/marketbroker:0.3.0 .
```


### Run

```bash
docker compose --profile infra up
```




# Grafana


## Data sources:

https://grafana.com/grafana/plugins/questdb-questdb-datasource/?tab=installation

```bash
grafana-cli plugins install questdb-questdb-datasource
```


## Dashboards:

JVM SpringBoot3 dashboard (for Prometheus Operator) ID: 22108


## Materialized views:

https://questdb.com/blog/how-to-create-a-materialized-view/

