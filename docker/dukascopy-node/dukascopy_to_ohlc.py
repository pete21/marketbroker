"""
Dukascopy tick-data decoder + OHLC bar builder.

Input format (per hour block):
    {
      "timestamp": <ms, start of hour>,
      "multiplier": <price unit for deltas, e.g. 0.001>,
      "ask": <starting ask price>,
      "bid": <starting bid price>,
      "times": [delta_ms, ...],   # ms since previous tick
      "asks":  [delta_units, ...] # price change since previous tick, in `multiplier` units
      "bids":  [delta_units, ...]
    }

All four arrays (times/asks/bids) are delta-encoded and must be
cumulatively summed (with times added to `timestamp`, asks added to
`ask`, bids added to `bid`) to recover the actual tick stream.

Usage:
    python dukascopy_ohlc.py input.json output.csv
    python dukascopy_ohlc.py input.json output.csv --bar-size 1s
"""

import argparse
import json
import sys
from pathlib import Path

import numpy as np
import pandas as pd


def load_blocks(path: str) -> list[dict]:
    """Load one or more hourly blocks. Accepts a single JSON object,
    a JSON array of objects, or a file with one JSON object per line
    (NDJSON), which is how Dukascopy dumps often arrive."""
    text = Path(path).read_text()
    text_stripped = text.strip()

    if text_stripped.startswith("["):
        return json.loads(text_stripped)

    # Try plain single-object JSON first.
    try:
        return [json.loads(text_stripped)]
    except json.JSONDecodeError:
        pass

    # Fall back to NDJSON (one object per line).
    blocks = []
    for line in text.splitlines():
        line = line.strip()
        if line:
            blocks.append(json.loads(line))
    return blocks


def decode_block(block: dict) -> pd.DataFrame:
    """Reconstruct the actual tick stream (timestamp, ask, bid) from one
    delta-encoded hourly block."""
    base_ts = block["timestamp"]
    multiplier = block["multiplier"]
    base_ask = block["ask"]
    base_bid = block["bid"]

    times = np.asarray(block["times"], dtype=np.int64)
    asks = np.asarray(block["asks"], dtype=np.float64)
    bids = np.asarray(block["bids"], dtype=np.float64)

    n = len(times)
    if not (len(asks) == n and len(bids) == n):
        raise ValueError(
            f"Mismatched array lengths in block at timestamp {base_ts}: "
            f"times={len(times)} asks={len(asks)} bids={len(bids)}"
        )

    ts = base_ts + np.cumsum(times)
    ask = base_ask + np.cumsum(asks) * multiplier
    bid = base_bid + np.cumsum(bids) * multiplier

    return pd.DataFrame({"timestamp_ms": ts, "ask": ask, "bid": bid})


def build_ohlc(ticks: pd.DataFrame, bar_size: str = "1s") -> pd.DataFrame:
    """Resample decoded ticks into OHLC bars on the mid price."""
    df = ticks.copy()
    df["time"] = pd.to_datetime(df["timestamp_ms"], unit="ms", utc=True)
    df["mid"] = (df["ask"] + df["bid"]) / 2
    df = df.set_index("time").sort_index()

    ohlc = df["mid"].resample(bar_size).ohlc()
    ohlc["tick_count"] = df["mid"].resample(bar_size).count()
    ohlc = ohlc.dropna(subset=["open"])  # drop empty buckets with no ticks
    ohlc = ohlc.round(3)
    ohlc["tick_count"] = ohlc["tick_count"].astype(int)

    return ohlc.reset_index()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input", help="Path to Dukascopy JSON tick file")
    parser.add_argument("output", help="Path to write OHLC CSV")
    parser.add_argument(
        "--bar-size",
        default="1s",
        help="Pandas offset alias for bar size, e.g. 1s, 1min, 5min (default: 1s)",
    )
    args = parser.parse_args()

    blocks = load_blocks(args.input)

    all_ticks = pd.concat(
        [decode_block(b) for b in blocks], ignore_index=True
    ).sort_values("timestamp_ms")

    print(f"Decoded {len(all_ticks)} ticks from {len(blocks)} block(s).", file=sys.stderr)

    ohlc = build_ohlc(all_ticks, bar_size=args.bar_size)
    print(f"Built {len(ohlc)} OHLC bars ({args.bar_size}).", file=sys.stderr)

    output_path = Path(args.output)
    file_exists = output_path.exists() and output_path.stat().st_size > 0

    ohlc.to_csv(
        output_path,
        index=False,
        mode="a" if file_exists else "w",
        header=not file_exists,
    )

    action = "Appended to" if file_exists else "Wrote"
    print(f"{action} {args.output}", file=sys.stderr)


if __name__ == "__main__":
    main()