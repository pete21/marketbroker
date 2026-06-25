#!/bin/bash

instrument_ids=(
    "6374"
    "16917"
    "872703"
)

symbols=(
    "deuidxeur"
    "usatechidxusd"
    "usa500idxusd"
)

for i in "${!instrument_ids[@]}"; do
    ./upload_to_quantdb.sh "${instrument_ids[$i]}" "${symbols[$i]}"
done

# ./upload_to_quantdb.sh 6374 deuidxeur
# ./upload_to_quantdb.sh 16917 usatechidxusd
# ./upload_to_quantdb.sh 872703 usa500idxusd