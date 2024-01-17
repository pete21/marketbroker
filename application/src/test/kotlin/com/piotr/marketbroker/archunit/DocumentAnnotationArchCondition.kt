package com.piotr.marketbroker.archunit

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import jakarta.persistence.Entity

class DocumentAnnotationArchCondition : ArchCondition<JavaClass>("satisfy @Document annotation conditions") {

    override fun check(javaClass: JavaClass, conditionEvents: ConditionEvents) {
        val documentAnnotation = javaClass.getAnnotationOfType(Entity::class.java)
        val regex = "^[a-z_]*$"

        val value = documentAnnotation.name

        if (!value.matches(Regex(regex))) {
            conditionEvents.add(
                SimpleConditionEvent.violated(
                    javaClass,
                    "[${javaClass.name}] incorrect value in annotation @Document (regex: $regex)"
                )
            )
        }
    }
}
