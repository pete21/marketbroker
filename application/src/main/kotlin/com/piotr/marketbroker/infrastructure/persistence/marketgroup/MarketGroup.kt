package com.piotr.marketbroker.infrastructure.persistence.marketgroup

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "marketgroup")
class MarketGroup (

    @Id
    @JsonProperty("ID")
    val id: Int = 0,

    @JsonProperty("__type")
    val type: String = "",

    @JsonProperty("Name")
    val name: String = "",

    @JsonProperty("IsSuperGroup")
    val isSuperGroup: Boolean = false,

    @JsonProperty("IsWhiteLabelPopularMarket")
    val isWhiteLabelPopularMarket: Boolean = false,

    @JsonProperty("HasSubscription")
    val hasSubscription: Boolean = false

    /*
    {
      "__type": "TradingPlatform.TreeMarketGroup",
      "ID": 991,
      "Name": "Popular Markets",
      "IsSuperGroup": true,
      "IsWhiteLabelPopularMarket": true,
      "HasSubscription": false
    }
 */
) {
    override fun toString(): String {
        return "MarketGroup(id=$id, type='$type', name='$name', isSuperGroup=$isSuperGroup, isWhiteLabelPopularMarket=$isWhiteLabelPopularMarket, hasSubscription=$hasSubscription)"
    }
}
