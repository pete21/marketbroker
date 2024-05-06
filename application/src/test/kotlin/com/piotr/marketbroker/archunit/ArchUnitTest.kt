package com.piotr.marketbroker.archunit

import ai.symmetrical.kafka.producer.FixedTopicMessageProducer
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.GeneralCodingRules
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

private const val MAIN_PACKAGE = "com.piotr.marketbroker"

@AnalyzeClasses(
    packages = ["com.piotr.marketbroker"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class ArchUnitTest {

    @ArchTest
    val `controllers are adapters` = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().resideInAPackage("..application.controller..")


    @ArchTest
    val `methods in rest controllers should have pre authorize annotation` = ArchRuleDefinition.methods()
        .that()
        .areDeclaredInClassesThat()
        .areAnnotatedWith(RestController::class.java)
        .and()
        .arePublic()
        .should()
        .beAnnotatedWith(PreAuthorize::class.java)

    @ArchTest
    @ArchIgnore
    val `the domain package does not use infrastucture packages` = ArchRuleDefinition.noClasses()
        .that()
        .resideInAPackage("$MAIN_PACKAGE.domain..")
        .should()
        .accessClassesThat()
        .resideInAnyPackage("$MAIN_PACKAGE.domain.infrastructure..")

    @ArchTest
    @ArchIgnore
    val `the domain package does not use application packages` = ArchRuleDefinition.noClasses()
        .that()
        .resideInAPackage("$MAIN_PACKAGE.domain..")
        .should()
        .accessClassesThat()
        .resideInAnyPackage("$MAIN_PACKAGE.application..")

    @ArchTest
    @ArchIgnore
    val `the domain package does not use fixed message producer` = ArchRuleDefinition.noClasses()
        .that()
        .resideInAPackage("$MAIN_PACKAGE.domain..")
        .should()
        .dependOnClassesThat()
        .areAssignableTo(FixedTopicMessageProducer::class.java)

    @ArchTest
    val `no classes should access standard streams` =
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS

    @ArchTest
    @ArchIgnore
    val `no classes should use field injection` =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION

    @ArchTest
    val `no classes should use java util logging` =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
}