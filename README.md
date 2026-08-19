# CFD and Crypto broker integration sdk + management + observability platform


## SDK

### Docker build

```bash
docker build -f ./Dockerfile -t url:5443/marketbroker:0.4.0 .
```


### Run

```bash
docker compose --profile infra up
```


### Refresh image of running container

```bash
docker-compose --profile sdk stop marketbroker && docker-compose --profile sdk up -d --no-deps marketbroker
```

```bash
docker compose --profile sdk stop marketbroker
docker compose --profile sdk rm -f marketbroker
docker compose --profile sdk up -d marketbroker
```




## kafka-connect

### Connector

Run:

```bash
./docker/kafka_connect.sh
```



## Grafana

### Data sources:

https://grafana.com/grafana/plugins/questdb-questdb-datasource/?tab=installation

```bash
grafana-cli plugins install questdb-questdb-datasource
```




## Observability and management


### Grafana
### Loki
### Prometheus
### kafka UI




## Dashboards:

JVM SpringBoot3 dashboard (for Prometheus Operator) ID: 22108



## Materialized views:

https://questdb.com/blog/how-to-create-a-materialized-view/




# STOMP Websocket server

Test: https://localhost:8080/
Select /topic/1





# Login flow

OAuth PKCE implemented:

- Authorize — GET authurl/oauth/authorize with PKCE (code_challenge, state, client_id, etc.)
- Keycloak login — parse the login form from the HTML and POST username / password to login-actions/authenticate
- Authorization code — read code from the redirect chain to tradenation.com/login/callback
- Token exchange — POST to authurl/oauth/token with grant_type=authorization_code and code_verifier

## Configuration

resources/*.properties
