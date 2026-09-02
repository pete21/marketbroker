package com.piotr.marketbroker.infrastructure.questdb

import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.questdb.QuestDbConfigurationProperties
import com.piotr.marketbroker.configuration.questdb.QuestDbPgDataSource
import com.piotr.marketbroker.domain.history.DataHistory
import com.piotr.marketbroker.domain.history.Ohlc
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.Date
import java.time.Duration

private const val CREATE_QUESTDB_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS %s(timestamp TIMESTAMP,q symbol CAPACITY 2,b float,a float) TIMESTAMP(timestamp) PARTITION BY DAY WAL DEDUP UPSERT KEYS(timestamp,q)"
private const val QUESTDB_TABLE_NAME_TEMPLATE = "TICKSTREAM_%d"

private const val TABLE_NAME_TEMPLATE = "DUKASCOPY_%s_OHLC_%s"
private const val GAPS_TABLE_NAME_TEMPLATE = "GAPS_%s_OHLC_%s"
private const val TICKSTREAM_TABLE_NAME_TEMPLATE = "TICKSTREAM_%s_OHLC_%s"

private const val GET_DATA_HISTORY_QUERY = """SELECT timestamp as date, open as Open, high as High, low as Low, close as Close FROM %s where timestamp>=?
UNION
select timestamp, first(open), max(high), min(low), last(close) from (
SELECT timestamp, first(open) as open, max(high) as high, min(low) as low, first(close) as close FROM
(
SELECT timestamp, open, high, low, close FROM %s where timestamp > (select max(timestamp) FROM  %s)
UNION
SELECT timestamp, open, high, low, close FROM %s where timestamp > (select max(timestamp) FROM %s)
) group by timestamp order by timestamp) sample by %s order by date desc limit 5000"""

private val TICKERS: Map<String, String> = mapOf(
    "DAX40" to "6374",
    "NQ100" to "16917",
    "SP500" to "872703",
)

private val IDENTIFIER = Regex("^[A-Za-z0-9_]+$")

private const val DEFAULT_NUM_CANDLES = 1000L

@Service
class QuestDbAdapter(
    private val questDbPgDataSource: QuestDbPgDataSource,
    private val questDbConfigurationProperties: QuestDbConfigurationProperties,
) {
    private val log by logger()

    // @Observed(name = "QuestDbAdapter",
    //     contextualName = "createTable",
    //     lowCardinalityKeyValues = ["type","questdb"]
    // )
    fun createTable(quoteId: Int): Boolean {
        val name = String.format(QUESTDB_TABLE_NAME_TEMPLATE, quoteId)
        val query = String.format(CREATE_QUESTDB_TABLE_QUERY, name)
        log.debug("Creating QuestDB table: $name query=$query")
        try {
            val connection = questDbPgDataSource.getConnection()
                connection.prepareStatement(query).use { statement ->
                    statement.execute()
                }
                connection.close()
        } catch (e: Exception) {
            log.error("Error creating QuestDB table: $name query=$query", e)
            return false
        }
        return true
    }

    // @Observed(name = "QuestDbAdapter",
    //     contextualName = "getDataHistory",
    //     lowCardinalityKeyValues = ["type","questdb"]
    // )
    fun getDataHistory(ticker: String, period: String, start: String, end: String): DataHistory {
        val tickerId = requireIdentifier(TICKERS[ticker] ?: ticker, "ticker")
        val sampleByPeriod = requireIdentifier(period, "period")
        val table = String.format(TABLE_NAME_TEMPLATE, tickerId, sampleByPeriod)
        val table1m = String.format(TABLE_NAME_TEMPLATE, tickerId, "1M")
        val gapsTable1m = String.format(GAPS_TABLE_NAME_TEMPLATE, tickerId, "1M")
        val tickstreamTable1m = String.format(TICKSTREAM_TABLE_NAME_TEMPLATE, tickerId, "1M")

        val query = String.format(
            GET_DATA_HISTORY_QUERY,
            table,
            gapsTable1m,
            table1m,
            tickstreamTable1m,
            table1m,
            sampleByPeriod,
        )
        log.debug("getDataHistory ticker=$ticker period=$period start=$start end=$end query=$query")

        return questDbPgDataSource.getConnection().use { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, start.ifBlank { 
                    val d: Duration = if (period.lowercase().contains("d")) {
                        Duration.parse("P${period}").multipliedBy(DEFAULT_NUM_CANDLES)
                    } else {
                        Duration.parse("PT${period}").multipliedBy(DEFAULT_NUM_CANDLES)
                    }
                    Date().toInstant().minus(d).toString()
                })
                statement.executeQuery().use { resultSet ->
                    DataHistory(ohlc = mapOhlcRows(resultSet))
                }
            }
        }
    }

    private fun mapOhlcRows(resultSet: ResultSet): List<Ohlc> {
        val rows = mutableListOf<Ohlc>()
        while (resultSet.next()) {
            val timestamp = resultSet.getTimestamp(1)
            rows += Ohlc(
                o = resultSet.getFloat(2),
                h = resultSet.getFloat(3),
                l = resultSet.getFloat(4),
                c = resultSet.getFloat(5),
                t = toUnixSeconds(timestamp),
            )
        }
        return rows.asReversed()
    }

    private fun toUnixSeconds(timestamp: Timestamp?): Int {
        if (timestamp == null) {
            return 0
        }
        return (timestamp.time / 1000).toInt()
    }

    private fun requireIdentifier(value: String, name: String): String {
        require(IDENTIFIER.matches(value)) { "Invalid $name: $value" }
        return value
    }
}
