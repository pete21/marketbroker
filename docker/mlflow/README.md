# MLflow On-Premise Deployment using Docker Compose
Easily deploy an MLflow tracking server with 1 command.

MinIO S3 is used as the artifact store and MySQL server is used as the backend store.

## How to run

3. Build and run the containers with `docker-compose`

    ```bash
    docker compose -f docker/mlflow/docker-compose.yml build mlflow
    ```

    ```bash
    docker compose up -d --build
    ```

4. Access MLflow UI with http://localhost:5000

5. Access MinIO UI with http://localhost:9901

## Containerization

The MLflow tracking server is composed of 4 docker containers:

* MLflow server
* MinIO object storage server
* MySQL database server


### Note

mlflow_server has a db migration problem. Run this:

`sql
UPDATE alembic_version SET version_num='1bd49d398cd23';
`

