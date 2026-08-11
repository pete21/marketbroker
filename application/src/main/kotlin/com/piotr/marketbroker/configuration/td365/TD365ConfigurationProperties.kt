package com.piotr.marketbroker.configuration.td365

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "td365")
data class TD365ConfigurationProperties(
    val demobaseurl: String,
    val prodbaseurl: String,
    val demoHeaders: Map<String, String>,
    val prodHeaders: Map<String, String>,
//    val demoReferer: String
//    val prodReferer: String
    val demowebsocketserver: String,
    val prodwebsocketserver: String,
    val authlink: String,
    val oauthauthorizeurl: String,
    val oauthtokenurl: String,
    val oauthclientid: String,
    val oauthredirecturi: String,
    val oauthaudience: String,
    val oauthuibrand: String,
    val loginlink: String,
    val accountlink: String,
    val logoutlink: String,
    val demologoutlink: String,
    val platformlogoutlink: String,
    val sessionupdateinterval: Int = 120000,
    val reauthenticateinterval: Int = 15000000,
    val accesstokenrefreshinterval: Int = 270000,
    val username: String,
    val password: String,
    val brokertimeutcdelta: Int = 1
//    override fun toString(): String {
//        return "TD365ConfigurationProperties(demolink='$demolink', demobaseurl='$demobaseurl', prodlink='$prodlink', prodbaseurl='$prodbaseurl', demoHeaders=$demoHeaders, prodHeaders=$prodHeaders, demowebsocketserver='$demowebsocketserver', prodwebsocketserver='$prodwebsocketserver', authlink='$authlink', accountlink='$accountlink', sessionupdateinterval=$sessionupdateinterval, username='$username', password='$password')"
//    }
    /* demoReferer='$demoReferer', prodReferer='$prodReferer',*/
)
