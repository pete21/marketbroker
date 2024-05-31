import pandas as pd
import sys

loadcols = ['timestamp', 'close']

mapping = {"deuidxeur": "6374",  # Germany 40
           "usa30idxusd": "6647",  # Wall Street 30
           "usatechidxusd": "16917",  # US Tech 100       roznica 3 punktow 31/05
           "usa500idxusd": "872703",  # US 500
           "gbridxgbp": "5945",  # UK 100
           "jpnidxjpy": "862708",  # JPY 225           roznica 8 punktow 31/05
           "brentcmdusd": "883341",  # Brent

           "plnidxpln": "100"  # Poland 20 Index
           }


def merge(_ticker, _from, _to, _period):
    print(f"Merging: {_ticker}, {_from}, {_to}, {_period}")

    bid = pd.read_csv(f'download/{_ticker}-{_period}-bid-{_from}-{_to}.csv', header=0, index_col=0, usecols=loadcols)
    ask = pd.read_csv(f'download/{_ticker}-{_period}-ask-{_from}-{_to}.csv', header=0, index_col=0, usecols=loadcols)

    # bid.head()
    # ask.head()

    combinecsv = pd.merge(bid, ask, on='timestamp', how='inner', suffixes=('bid', 'ask'))

    #    combinecsv['m'] = combinecsv[
    #        ['openbid', 'highbid', 'lowbid', 'closebid', 'openask', 'highask', 'lowask', 'closeask']].mean(axis=1).round(5)

    combinecsv.index = combinecsv.index * 1000
    combinecsv = combinecsv.rename(columns={'closebid': 'b', 'closeask': 'a'})
    combinecsv["q"] = mapping.get(_ticker, _ticker)

    combinecsv.to_csv(f"download/{_ticker}-{_period}-{_from}-{_to}.csv", columns=["q", "b", "a"])


# pandas.merge(left, right, how='inner', on=None, left_on=None, right_on=None, left_index=False, right_index=False, sort=False, suffixes=('_x', '_y'), copy=None, indicator=False, validate=None)


if __name__ == '__main__':
    merge(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
