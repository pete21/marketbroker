package com.piotr.marketbroker.configuration.td365

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "td365")
class TD365ConfigurationProperties {
    lateinit var demolink: String
    lateinit var demobaseurl: String
    lateinit var prodlink: String
    lateinit var prodbaseurl: String
    lateinit var demoHeaders: Map<String, String>
    lateinit var prodHeaders: Map<String, String>
//    lateinit var demoReferer: String
//    lateinit var prodReferer: String
    lateinit var demowebsocketserver: String
    lateinit var prodwebsocketserver: String
    lateinit var authlink: String
    lateinit var accountlink: String
     var sessionupdateinterval: Int = 120000
    lateinit var username: String
    lateinit var password: String
    override fun toString(): String {
        return "TD365ConfigurationProperties(demolink='$demolink', demobaseurl='$demobaseurl', prodlink='$prodlink', prodbaseurl='$prodbaseurl', demoHeaders=$demoHeaders, prodHeaders=$prodHeaders, demowebsocketserver='$demowebsocketserver', prodwebsocketserver='$prodwebsocketserver', authlink='$authlink', accountlink='$accountlink', sessionupdateinterval=$sessionupdateinterval, username='$username', password='$password')"
    }
    /* demoReferer='$demoReferer', prodReferer='$prodReferer',*/
}
