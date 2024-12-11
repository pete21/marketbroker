#!/bin/sh

docker run --add-host=host.docker.internal:host-gateway -p 8888:8888 -p 2000-2010:2000-2010 -v /Users/tzieleniewski/Private/repos/marketbroker2/notebooks/:/opt/notebooks anaconda3_py10_ml:2
