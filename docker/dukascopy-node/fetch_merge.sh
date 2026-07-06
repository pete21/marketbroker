#!/bin/sh
npx dukascopy-node -i $1 -r 2 -p bid -ch true -from $2 -to $3 -t $4 -f csv      # -d
npx dukascopy-node -i $1 -r 2 -p ask -ch true -from $2 -to $3 -t $4 -f csv      # -d
python merge.py $1 $2 $3 $4
