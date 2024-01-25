package com.piotr.marketbroker.infrastructure.persistence.marketgroup

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "marketgroup")
class MarketGroup (

    @Id @GeneratedValue var id: Int? = null,

    val type: String? = null,

    val name: String? = null,

    val isSuperGroup: Boolean = false,

    val isWhiteLabelPopularMarket: Boolean = false,

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
)
