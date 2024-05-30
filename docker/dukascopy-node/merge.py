import pandas as pd
import sys

loadcols = ['timestamp', 'close']

def merge(_ticker, _from, _to, _period):
    print(f"Merging: {_ticker}, {_from}, {_to}, {_period}")

    bid = pd.read_csv(f'download/{_ticker}-{_period}-bid-{_from}-{_to}.csv', header=0, index_col=0, usecols=loadcols)
    ask = pd.read_csv(f'download/{_ticker}-{_period}-ask-{_from}-{_to}.csv', header=0, index_col=0, usecols=loadcols)

    # bid.head()
    # ask.head()

    combinecsv = pd.merge(bid, ask, on='timestamp', how='inner', suffixes=('bid', 'ask'))

#    combinecsv['m'] = combinecsv[
#        ['openbid', 'highbid', 'lowbid', 'closebid', 'openask', 'highask', 'lowask', 'closeask']].mean(axis=1).round(5)
#    combinecsv['timestamp'] = combinecsv['timestamp'].multiply(1000)
    combinecsv.index = combinecsv.index*1000
    combinecsv = combinecsv.rename(columns={'closebid': 'b', 'closeask': 'a'})

    combinecsv.to_csv(f"download/{_ticker}-{_period}-{_from}-{_to}.csv", columns=["b", "a"])


# pandas.merge(left, right, how='inner', on=None, left_on=None, right_on=None, left_index=False, right_index=False, sort=False, suffixes=('_x', '_y'), copy=None, indicator=False, validate=None)


if __name__ == '__main__':
    merge(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
