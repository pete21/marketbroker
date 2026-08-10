#!/bin/bash

instrument_ids=(
    "6374"
    "16917"
    "872703"
)

symbols=(
    "DEU.IDX-EUR"
    "USATECH.IDX-USD"
    "USA500.IDX-USD"
)

for i in "${!instrument_ids[@]}"; do
    ./upload_to_quantdb2.sh "${instrument_ids[$i]}" "${symbols[$i]}"
done

# ./upload_to_quantdb2.sh 6374 DEU.IDX-EUR
# ./upload_to_quantdb2.sh 16917 USATECH.IDX-USD
# ./upload_to_quantdb2.sh 872703 USA500.IDX-USD