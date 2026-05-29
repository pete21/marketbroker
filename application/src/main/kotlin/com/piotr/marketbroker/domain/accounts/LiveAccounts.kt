package com.piotr.marketbroker.domain.accounts

import com.fasterxml.jackson.annotation.JsonProperty


// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

//class About {
//    var date_of_birth: String? = null
//    var addr_street: String? = null
//    var addr_line_2: String? = null
//    var addr_city: String? = null
//    var addr_zip: String? = null
//    var swap_free_status: String? = null
//    var kyc_status: String? = null
//    var kyc_last_updated_date: String? = null
//    var birth_country_id: Any? = null
//    var gender: Any? = null
//    var province: Any? = null
//    var kyc_reminder_sent: Boolean = false
//    var kyc_final_reminder_sent: Boolean = false
//    var prt_connected: Any? = null
//}

class AppMetadata {
//    var clients: ArrayList<Client?>? = null
    var default_currency: DefaultCurrency? = null
//    var previous_experience: String? = null
    var trading_accounts: ArrayList<TradingAccount?>? = null
    var has_getprofile_error: Boolean = false
}

class Backend {
    var id: Int = 0
    var name: String? = null
    var derivative_type: String? = null
    var display_name: String? = null
    var type: String? = null
    var mode: String? = null
}

class Balance {
    var cash_balance: String? = null
    var currency: String? = null
    var total_equity: String? = null
    var withdrawable_amount: String? = null
    var total_credit: String? = null
}

//class Client {
//    var available: Boolean = false
//    var brand: String? = null
//    var type: String? = null
//    var state: String? = null
//    var client_profile_id: Int = 0
//}

class DefaultCurrency {
    var bs: String? = null
}

class Identity {
    var user_id: String? = null
    var provider: String? = null
    var connection: String? = null
    var isSocial: Boolean = false
}

//class Kyc {
//    var employment_status: String? = null
//    var employment_industry: String? = null
//    var job_role: String? = null
//    var fund_source: String? = null
//    var annual_income: String? = null
//    var asset_worth: String? = null
//}

//class Personal {
//    var first_name: String? = null
//    var last_name: String? = null
//    var telephone: String? = null
//    var addr_country: String? = null
//    var addr_country_full: String? = null
//    var nationality: Any? = null
//    var nationality_full: Any? = null
//    var birth_country: String? = null
//    var birth_country_full: String? = null
//    var place_of_birth: Any? = null
//    var language: Any? = null
//    var title: String? = null
//}

class LiveAccounts {
    var email: String? = null
    var email_verified: Boolean = false
    var identities: ArrayList<Identity?>? = null
    var name: String? = null
    var nickname: String? = null
    var user_id: String? = null
//    var user_metadata: UserMetadata? = null
    var app_metadata: AppMetadata? = null
}

class TradingAccount {
    var external_id: String? = null
    var client_profile_id: Int = 0
    var type: String? = null
    var brand: String? = null
    var alpha_code: String? = null
    var created: String? = null
    var expires_at: String? = null

    @JsonProperty("LoginName")
    var loginName: String? = null
    var account_id: String? = null
    var platform: String? = null
    var backend: Backend? = null
    var balance: Balance? = null
    var id: Int = 0
    var display_name: String? = null
    var username: String? = null
    var currency: String? = null
    var is_allowed_bonus: Any? = null
    var ct_login_id: String? = null
    var ct_login_password: String? = null
    var mt4_user_group: String? = null
    var platform_url: String? = null
    var is_allowed_deposit: Boolean = false
    var is_allowed_withdrawal: Boolean = false
    var status: String? = null
}

//class UserMetadata {
//    var first_name: String? = null
//    var last_name: String? = null
//    var telephone: String? = null
//    var has_deposited: Boolean = false
//    var cxdRef: String? = null
//    var last_deposit_date: String? = null
//    var last_withdrawal_date: String? = null
//    var last_traded_date: String? = null
//    var seven_day_login_counts: Int = 0
//    var date_joined: String? = null
//    var about: About? = null
//    var personal: Personal? = null
//    var kyc: Kyc? = null
//}








/*
@JsonPropertyOrder("count", "next", "previous", "results")
data class LiveAccounts (
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
    "ct_login_password",
    "is_allowed_deposit",
    "is_allowed_withdrawal"
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
    var ctLoginId: String?,

    @JsonProperty("ct_login_password")
    var ctLoginPassword: String?,

    @JsonProperty("is_allowed_deposit")
    var isAllowedDeposit: Boolean,

    @JsonProperty("is_allowed_withdrawal")
    var isAllowedWithdrawal: Boolean
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
*/
