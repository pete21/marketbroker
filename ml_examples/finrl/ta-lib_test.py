import numpy as np
import talib

import talib as ta
import yfinance as yf
aapl = yf.download('AAPL', '2019-1-1','2019-12-27')
aapl['Simple MA'] = ta.SMA(aapl['Close'].squeeze(), 14)
aapl['EMA'] = ta.EMA(aapl['Close'].squeeze(), timeperiod = 14)
print(aapl.tail())


close = np.random.random(100)
#Calculate a simple moving average of the close prices:

output = ta.SMA(close)