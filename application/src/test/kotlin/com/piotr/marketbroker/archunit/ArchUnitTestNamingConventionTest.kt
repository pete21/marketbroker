package com.piotr.marketbroker.archunit

import com.tngtech.archunit.core.importer.ImportOption.OnlyIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import io.kotest.core.spec.style.ExpectSpec

@AnalyzeClasses(
    packages = ["com.piotr.marketbroker"],
    importOptions = [
        OnlyIncludeTests::class,
        DoNotIncludeBaseTest::class
    ]
)
class ArchUnitTestNamingConventionTest {

    @ArchTest
    @ArchIgnore
    val `test classes should end with test` = ArchRuleDefinition.classes()
        .that()
        .areAssignableTo(ExpectSpec::class.java)
        .should()
        .haveNameMatching(".*Test\$")
}