package com.piotr.marketbroker.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location

class DoNotIncludeBaseTest : ImportOption {

    override fun includes(location: Location): Boolean {
        return !location.contains("BaseTest")
    }
}