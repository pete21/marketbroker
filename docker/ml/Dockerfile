FROM continuumio/anaconda3:2023.09-0

# Use C.UTF-8 locale to avoid issues with ASCII encoding
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

RUN apt update && apt install -y build-essential zip python-dev libsdl2-dev
RUN apt install -y cmake libopenmpi-dev python3-dev zlib1g-dev libgl1-mesa-glx swig vim && rm -rf /var/lib/apt/lists/*

RUN conda install -y conda-forge::xvfbwrapper

#USER root
WORKDIR /root

# TA-lib
#COPY ta-lib-0.4.0-src.tar.gz .
#RUN tar xvf ./ta-lib-0.4.0-src.tar.gz
#RUN rm ./ta-lib-0.4.0-src.tar.gz
#WORKDIR /root/ta-lib
#RUN ./configure --prefix=/usr
#RUN make && make install

RUN wget https://github.com/ta-lib/ta-lib/releases/download/v0.6.3/ta-lib-0.6.3-src.tar.gz
RUN tar -xvf ./ta-lib-0.6.3-src.tar.gz
WORKDIR /root/ta-lib-0.6.3
RUN ./configure
RUN make
RUN make install


WORKDIR /root

RUN --mount=type=cache,target=/root/.cache/pip pip install TA-Lib finta

RUN --mount=type=cache,target=/root/.cache/pip pip install pyopengl ffmpeg

# FinRL
RUN --mount=type=cache,target=/root/.cache/pip pip install git+https://github.com/AI4Finance-Foundation/FinRL.git
#RUN --mount=type=cache,target=/root/.cache/pip pip install git+https://github.com/AI4Finance-LLC/FinRL-Library.git

RUN --mount=type=cache,target=/root/.cache/pip pip install git+https://github.com/AI4Finance-LLC/ElegantRL.git

RUN --mount=type=cache,target=/root/.cache/pip pip install box2d stable-baselines3[extra] sb3-contrib ray[rllib] ray[default] rl_zoo3 finrl-meta dm-tree

# RUN --mount=type=cache,target=/root/.cache/pip pip install mpi4py
RUN --mount=type=cache,target=/root/.cache/pip pip install gymnasium gymnasium[box2d] yfinance gym shimmy pygame panda_gym pandas_market_calendars

RUN --mount=type=cache,target=/root/.cache/pip pip install optuna tradingenv tradingenv[extra]

RUN --mount=type=cache,target=/root/.cache/pip pip install huggingface_sb3 huggingface_hub

RUN --mount=type=cache,target=/root/.cache/pip pip install pyvirtualdisplay pyyaml imageio imageio_ffmpeg pyglet tqdm

#RUN python3 -mpip install -U psutil dill joblib
#RUN python3 -mpip install -U setuptools wheel build scikit-build
#RUN python3 -mpip install -U numpy pandas pandas-datareader
#RUN python3 -mpip install -U scikit-learn
RUN --mount=type=cache,target=/root/.cache/pip pip install matplotlib seaborn prophet ipympl

RUN --mount=type=cache,target=/root/.cache/pip pip install pytest tox pyyaml dash flask flask_restful connexion requests
RUN --mount=type=cache,target=/root/.cache/pip pip install tensorflow torch torch_geometric
RUN --mount=type=cache,target=/root/.cache/pip pip install mlflow
RUN --mount=type=cache,target=/root/.cache/pip pip install lightgbm mlxtend catboost xgboost

RUN --mount=type=cache,target=/root/.cache/pip pip install elegantrl tensorboardX wrds ccxt

RUN --mount=type=cache,target=/root/.cache/pip pip install opentraveldata OpenTrepWrapper Neobase
RUN --mount=type=cache,target=/root/.cache/pip pip install elasticsearch kafka-python
RUN --mount=type=cache,target=/root/.cache/pip pip install questdb psycopg[binary] psycopg2-binary confluent-kafka confluent-kafka[avro] fastavro

RUN --mount=type=cache,target=/root/.cache/pip pip install openai langchain stockstats pyfolio-reloaded quantstats wrds pyportfolioopt
RUN --mount=type=cache,target=/root/.cache/pip pip install bottleneck --upgrade
# RUN pip install tensorrt

RUN --mount=type=cache,target=/root/.cache/pip pip install boto3

RUN --mount=type=cache,target=/root/.cache/pip pip install pandas-market-calendars IProgress

RUN mkdir /root/.aws
COPY credentials ./.aws/

RUN rm /usr/lib/x86_64-linux-gnu/libstdc++.so.6
RUN cp /opt/conda/lib/libstdc++.so.6.0.29 /usr/lib/x86_64-linux-gnu/
RUN ln -s /usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.29 /usr/lib/x86_64-linux-gnu/libstdc++.so.6

ENV MLFLOW_TRACKING_URI=http://host.docker.internal:5000
ENV MLFLOW_S3_ENDPOINT_URL=http://host.docker.internal:9900

ENV LD_LIBRARY=/usr/lib/x86_64-linux-gnu/

EXPOSE 8888
EXPOSE 2000-2010

CMD ["bash", "-c", "jupyter lab \
    --notebook-dir=/opt/notebooks --ip=0.0.0.0 --port=8888 \
    --no-browser --allow-root"]
