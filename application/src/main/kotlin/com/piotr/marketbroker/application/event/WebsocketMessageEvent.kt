package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.infrastructure.websocket.WebsocketDTO

class WebsocketMessageEvent(val message: WebsocketDTO)
