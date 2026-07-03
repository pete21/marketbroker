# marketbroker



### Docker build

```bash
docker build -f ./Dockerfile -t registry.tradefiapp.com:5443/marketbroker:0.3.0 .
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


# kafka-connect

## Connector

Run:

```bash
./docker/kafka_connect.sh
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




# STOMP Websocket server








# Login flow

The old JSON POST to tradenation.com/signup/api/login is replaced with OAuth PKCE:

- Authorize — GET auth.tradenation.com/oauth/authorize with PKCE (code_challenge, state, client_id, etc.)
- Keycloak login — parse the login form from the HTML and POST username / password to login-actions/authenticate
- Authorization code — read code from the redirect chain to tradenation.com/login/callback
- Token exchange — POST to auth.tradenation.com/oauth/token with grant_type=authorization_code and code_verifier

## Configuration

td365.oauthauthorizeurl=https://auth.tradenation.com/oauth/authorize
td365.oauthtokenurl=https://auth.tradenation.com/oauth/token
td365.oauthclientid=n1uzY72W6TxZ1GaT0YUKVa1uCcw60pNo
td365.oauthredirecturi=https://tradenation.com/login/callback
td365.oauthaudience=https://api.tradenation.com
td365.oauthuibrand=en-eu

