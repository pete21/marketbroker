package com.piotr.marketbroker.common

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.Instant

open class BaseEntity {

    @Version
    var version: Int? = null

    @CreatedDate
    var createdAt: Instant? = null

    @CreatedBy
    var createdBy: Auditor? = null

    @LastModifiedDate
    var modifiedAt: Instant? = null

    @LastModifiedBy
    var modifiedBy: Auditor? = null

    inline fun <reified T : BaseEntity> T.copyAuditingInfo(): T {
        return this.apply {
            this.version = this@BaseEntity.version
            this.createdAt = this@BaseEntity.createdAt
            this.createdBy = this@BaseEntity.createdBy
            this.modifiedAt = this@BaseEntity.modifiedAt
            this.modifiedBy = this@BaseEntity.modifiedBy
        }
    }
}
