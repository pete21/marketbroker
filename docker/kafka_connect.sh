!/bin/bash

curl -s https://api.github.com/repos/questdb/kafka-questdb-connector/releases/latest |
jq -r '.assets[]|select(.content_type == "application/zip")|.browser_download_url'|
wget -qi -

cd ./kafka_connect_plugins/kafka-questdb-connector
unzip ../../kafka-questdb-connector-*-bin.zip
#cp ./*.jar /path/to/kafka_*.*-*.*.*/libs
cd ../..
