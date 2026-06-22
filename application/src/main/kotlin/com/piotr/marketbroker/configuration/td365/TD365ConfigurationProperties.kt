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
    val loginlink: String,
    val accountlink: String,
    val logoutlink: String,
    val sessionupdateinterval: Int = 90000,
    val accesstokenupdateinterval: Int = 36000000,            //86400
    val username: String,
    val password: String
//    override fun toString(): String {
//        return "TD365ConfigurationProperties(demolink='$demolink', demobaseurl='$demobaseurl', prodlink='$prodlink', prodbaseurl='$prodbaseurl', demoHeaders=$demoHeaders, prodHeaders=$prodHeaders, demowebsocketserver='$demowebsocketserver', prodwebsocketserver='$prodwebsocketserver', authlink='$authlink', accountlink='$accountlink', sessionupdateinterval=$sessionupdateinterval, username='$username', password='$password')"
//    }
    /* demoReferer='$demoReferer', prodReferer='$prodReferer',*/
)
