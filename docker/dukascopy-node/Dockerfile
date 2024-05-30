ARG NODE_VERSION=20.13.1-alpine3.20

#FROM node:${NODE_VERSION}
FROM nikolaik/python-nodejs:python3.12-nodejs20-alpine

RUN mkdir -p /home/pn/app/node_modules && chown -R pn:pn /home/pn/app

WORKDIR /home/pn/app

USER pn
RUN mkdir download
RUN mkdir .dukascopy-cache

RUN pip install pandas
RUN npm install dukascopy-node --save

COPY --chown=pn:pn ./merge.py .
COPY --chown=pn:pn ./fetch_merge.sh .

# ENTRYPOINT [ "npx", "dukascopy-node" ]
