package com.piotr.marketbroker.infrastructure.persistence.subscription

import org.springframework.data.repository.CrudRepository

interface SpringDataSubscriptionsRepository: CrudRepository<Subscription, Int>