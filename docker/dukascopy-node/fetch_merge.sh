#!/bin/sh
npx dukascopy-node -i $1 -r 1 -p bid -ch true -from $2 -to $3 -t $4 -f csv -re
npx dukascopy-node -i $1 -r 1 -p ask -ch true -from $2 -to $3 -t $4 -f csv -re
python merge.py $1 $2 $3 $4
