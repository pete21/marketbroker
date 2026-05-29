#!/bin/sh

docker run --add-host=host.docker.internal:host-gateway -p 8888:8888 -p 2000-2010:2000-2010 -v /Users/tzieleniewski/Private/repos/marketbroker2/notebooks/:/home/jovyan/notebooks -v /Users/tzieleniewski/Private/repos/marketbroker2/ml_examples/:/home/jovyan/examples ml_11:v1.10_cuda-12.9_ubuntu-24.04


# docker run -p 8888:8888 -p 2000-2010:2000-2010 -v $(pwd):/home/jovyan/notebooks ml_11:v1.10_cuda-12.9_ubuntu-24.04
