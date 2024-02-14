package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder


@JsonPropertyOrder("count", "next", "previous", "results")
class RealAccounts (
    @JsonProperty("count")
    var count: Int,

    @JsonProperty("next")
    var next: Int?,

    @JsonProperty("previous")
    var previous: Int?,

    @JsonProperty("results")
    var results: List<Result>,
)


@JsonPropertyOrder(
    "id",
    "platform",
    "platformIcon",
    "account",
    "backend",
    "accountType",
    "currency",
    "currencySymbol",
    "balance",
    "equity",
    "button",
    "paymentsLink",
    "ct_login_id",
    "ct_login_password"
)
class Result (
    @JsonProperty("id")
    var id: Int,

    @JsonProperty("platform")
    var platform: String,

    @JsonProperty("platformIcon")
    var platformIcon: String,

    @JsonProperty("account")
    var account: String,

    @JsonProperty("backend")
    var backend: String,

    @JsonProperty("accountType")
    var accountType: String,

    @JsonProperty("currency")
    var currency: String,

    @JsonProperty("currencySymbol")
    var currencySymbol: String,

    @JsonProperty("balance")
    var balance: String,

    @JsonProperty("equity")
    var equity: String,

    @JsonProperty("button")
    var button: Button,

    @JsonProperty("paymentsLink")
    var paymentsLink: String,

    @JsonProperty("ct_login_id")
    var ctLoginId: String,

    @JsonProperty("ct_login_password")
    var ctLoginPassword: String,
)


@JsonPropertyOrder(
    "text", "type", "linkTo"
)
class Button (
    @JsonProperty("text")
    var text: String,

    @JsonProperty("type")
    var type: String,

    @JsonProperty("linkTo")
    var linkTo: String,
)
