package com.piotr.marketbroker.infrastructure.persistence.subscription

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="subscription")
class Subscription (

    @Id
    val quoteId: Int = 0,

    //    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    private Integer id;
    // Unidirectional
    //    @OneToOne(cascade = CascadeType.DETACH)
    //    @JoinColumn(name = "quoteID", referencedColumnName = "quoteID")
    //    private MarketQuote marketQuote;
    var status: Boolean = false
)
