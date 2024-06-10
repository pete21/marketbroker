# Example MLflow project

## Set up mlflow library

1. Install [conda](https://conda.io/projects/conda/en/latest/user-guide/install/index.html)

2. Install MLflow with extra dependencies, including scikit-learn

    ```bash
    pip install mlflow boto3
    ```

3. Set environmental variables

    ```bash
    export MLFLOW_TRACKING_URI=http://localhost:5000
    export MLFLOW_S3_ENDPOINT_URL=http://localhost:9000
    ```
4. Set MinIO credentials

    ```bash
    cat <<EOF > ~/.aws/credentials
    [default]
    aws_access_key_id=minio
    aws_secret_access_key=minio123
    EOF
    ```



## Run & serve model

5. Train a sample MLflow model

    ```bash
    mlflow run . -P alpha=0.42
    ```

    ```bash
    python train.py 0.5 0.1
    ```

6. Serve the model (replace ${MODEL_ID} with your model's ID)
   ```bash
   export MODEL_ID=0ced24069348417fbbcb2cd41a7d2f07 # Replace this with your model's ID
   # mlflow models serve -m runs:/${MODEL_ID}/model -p 2000 -h 0.0.0.0 --env-manager conda
   mlflow models serve -m runs:/${MODEL_ID}/model -p 2000 -h 0.0.0.0 --env-manager=local
   ```

7. You can check the input with this command
   ```bash
   curl -X POST -H "Content-Type:application/json" --data '{"dataframe_split":{"columns":["fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides", "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"],"data":[[6.2, 0.66, 0.48, 1.2, 0.029, 29, 75, 0.98, 3.33, 0.39, 12.8]]}}' http://127.0.0.1:2000/invocations
   ```
