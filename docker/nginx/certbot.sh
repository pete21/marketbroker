#!/bin/bash
#sudo certbot certonly --manual --preferred-challenges=dns --email contact@tradefiapp.com --server https://acme-v02.api.letsencrypt.org/directory --agree-tos -d trade.tradefiapp.com

docker run -it --rm --name certbot -v "$(pwd)/certbot:/etc/letsencrypt" certbot/certbot:v5.2.2 certonly \
--manual --preferred-challenges dns --email "contact@tradefiapp.com" --agree-tos --domain "trade.tradefiapp.com"
#            -v "/var/lib/letsencrypt:/var/lib/letsencrypt"

