#!/bin/bash

ACCOUNT_ID=$1
INSTRUMENT_ID=$2         # Instrument IDs as a string: "6374,16917,872703"

# Login
echo "Logging in"

curl --location 'https://data.tradefiapp.com/live/login' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--header 'Authorization: Basic dHJhZGVmaWFwcF91c2VyOnBhc3N3b3Jk' \
--data-raw '{
  "state": "START",
  "login": "piotr.nazarewicz@gmail.com",
  "password": "<string>"
}'

sleep 10

# Start session
echo "Starting session for account $ACCOUNT_ID"

curl --location 'https://data.tradefiapp.com/live/session' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
  "state": "START",
  "accountId": $ACCOUNT_ID
}'

sleep 5

# Subscribe instruments
echo "Subscribing instruments"

curl --location 'https://data.tradefiapp.com/instruments/subscriptions' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic dHJhZGVmaWFwcF91c2VyOnBhc3N3b3Jk' \
--data '{
    "quoteId":[$INSTRUMENT_ID],
    "status":true
}'

