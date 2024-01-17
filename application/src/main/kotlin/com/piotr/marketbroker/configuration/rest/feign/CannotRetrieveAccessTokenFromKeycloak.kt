package com.piotr.marketbroker.configuration.rest.feign

class CannotRetrieveAccessTokenFromKeycloak(name: String) :
    RuntimeException("Cannot retrieve access token for client $name")
