import json
from pathlib import Path
from urllib.parse import urlencode

from mitmproxy.script import concurrent

from mitmproxy import ctx, http


SYMBOL_TO_QUERYID_TABLE = {
    "SOLUSDT": "16917",
    "ETHUSDT": "16917",
    "BTCUSDT": "16917",
    "NQ100USDT": "16917",
    "DAX40USDT": "6374",
    "SP500USDT": "0",
    "WIG20USDT": "0",
}


@concurrent
def response(flow: http.HTTPFlow) -> None:

    if flow.request.path.startswith("/exec"):
        if not flow.response or not flow.response.content:
            return
        content = json.loads(flow.response.content)
#        print(content)

        # """
        # {"dataset":[[1767138900000,25450.879,25452.262,25450.24,25451.129,1],[1767138600000,25447.996,25451.271,25447.629,25450.668,1],[1767138300000,25451.367,25451.914,25444.852,25448.098,1],[1767138000000,25442.508,25451.375,25442.375,25451.25,1],[1767137700000,25454.5,25457.39,25438.488,25441.988,1],[1767137400000,25459.023,25460.625,25455.629,25455.734,1],[1767137100000,25460.723,25465.656,25457.84,25458.89,1],[1767136800000,25458.357,25461.467,25456.012,25460.906,1],[1767136500000,25455.646,25462.586,25454.37,25458.379,1],[1767136200000,25479.113,25479.652,25452.395,25456.984,1]],"count":10}
        # """

        dataset = content["dataset"]

        flow.response = http.Response.make(
            200,
            json.dumps(dataset),
            {"Content-Type": "application/json"})

    if flow.request.path.endswith("/api/v3/exchangeInfo"):
        if not flow.response or not flow.response.content:
            return
        content = json.loads(flow.response.content)
        # print(f"content: {content}")
        cfd_symbols = json.load(Path("./config/cfd_symbols.json").open())
        # print(f"nq: {nq}")
        content["symbols"].extend(cfd_symbols)
        print(f"adding {len(cfd_symbols)} symbols to response")
        flow.response = http.Response.make(
            200,
            json.dumps(content),
            {"Content-Type": "application/json"})
    if flow.request.path.endswith("/fapi/v1/exchangeInfo"):
        if not flow.response or not flow.response.content:
            return
        content = json.loads(flow.response.content)
        # print(f"content: {content}")
        cfd_symbols = json.load(Path("./config/cfd_symbols_fapi_v1.json").open())
        # print(f"nq: {nq}")
        content["symbols"].extend(cfd_symbols)
        print(f"adding {len(cfd_symbols)} symbols to response")
        flow.response = http.Response.make(
            200,
            json.dumps(content),
            {"Content-Type": "application/json"})

def request(flow: http.HTTPFlow) -> None:
    print("request", flow.request.path)
    print(flow.request)

    if flow.request.path.startswith("/fapi/v1/klines"):              # /fapi/v1/klines?interval=1m&limit=1500&symbol=SOLUSDT
# curl -G --data-urlencode "query=SELECT cast(timestamp AS long)/1000L date, open, high, low, close, 1 FROM DUKASCOPY_16917_OHLC_5M ORDER BY timestamp DESC" --data-urlencode "limit=10" --data-urlencode "nm=true" --data-urlencode "count=false" http://localhost:9000/exec

        ctx.log( flow.request.path )
        query_string = flow.request.path.split('?')[1]
        interval = query_string.split('&')[0].split('=')[1]
        limit = query_string.split('&')[1].split('=')[1]
        symbol = query_string.split('&')[2].split('=')[1]

        query_params = [
            ("query", f"SELECT cast(timestamp AS long)/1000L date, open, high, low, close, 1 as 'volume' FROM DUKASCOPY_{SYMBOL_TO_QUERYID_TABLE[symbol]}_OHLC_{interval.upper()} ORDER BY timestamp DESC LIMIT {limit}"),
            ("count", "false"),
            ("nm", "true"),
        ]
        flow.request.path = "/exec?" + urlencode(query_params)
        # flow.request.urlencoded_form = []
        flow.request.method = "GET"
        flow.request.host = "questdb"
        flow.request.port = 9000
        flow.request.scheme = 'http'
        flow.request.headers["Host"] = "questdb"
