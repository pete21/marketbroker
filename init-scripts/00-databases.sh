#!/bin/bash
# init-scripts/00-databases.sh
set -e

mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS $MLFLOW_DATABASE;
    GRANT ALL PRIVILEGES ON $MLFLOW_DATABASE.* TO '$MYSQL_USER'@'%';
    
    CREATE DATABASE if not EXISTS trade_db;
    GRANT ALL PRIVILEGES ON trade\_db.* TO '$MYSQL_USER'@'%';

    CREATE TABLE trade_db.inference (
        id int AUTO_INCREMENT,
        inference_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        timeseries_datetime DATETIME NOT NULL,
        prediction FLOAT NOT NULL,
        registered_model_name VARCHAR(40),
        model_version smallint,
        PRIMARY KEY (id),
        UNIQUE KEY (timeseries_datetime)
    );
    CREATE TABLE trade_db.orders (id INT NOT NULL AUTO_INCREMENT, inference_id int, signal_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, timeseries_datetime DATETIME NOT NULL, ticker VARCHAR(20) NOT NULL, side INT NOT NULL, price DECIMAL(8,6) NOT NULL, amount DECIMAL(6,2) NOT NULL, tp DECIMAL(8,6) NULL DEFAULT NULL, sl DECIMAL(8,6) NULL DEFAULT NULL, status SMALLINT NOT NULL, PRIMARY KEY (id)) ENGINE = InnoDB;

    FLUSH PRIVILEGES;
EOSQL