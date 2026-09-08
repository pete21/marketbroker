#!/bin/bash

ACCOUNT_ID=$1

# Stop session
echo "Stopping session for account $ACCOUNT_ID"

curl --location 'https://data.tradefiapp.com/live/session' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
  "state": "STOP",
  "accountId": $ACCOUNT_ID
}'


# Logout
echo "Logging out for account $ACCOUNT_ID"

curl --location 'https://data.tradefiapp.com/live/login' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
  "state": "STOP",
  "login": "test@tradefiapp.com",
  "password": "<string>"
}'

