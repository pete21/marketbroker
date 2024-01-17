package com.piotr.marketbroker

import org.springframework.core.io.Resource
import org.testcontainers.shaded.org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

object ResourceRetriever {

    fun getResource(resource: Resource): String {
        return getResource(resource.file)
    }

    fun getResource(file: File): String {
        return FileUtils.readFileToString(file, Charset.defaultCharset())
    }
}
