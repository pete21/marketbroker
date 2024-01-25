package com.piotr.marketbroker.infrastructure.persistence.tick

import org.springframework.data.repository.CrudRepository

interface SpringDataTickRepository :CrudRepository<Tick, Int>