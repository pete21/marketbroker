package com.piotr.marketbroker.common

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
object ReflectionMapHelper {

    fun <T : Any> toMap(obj: T): Map<String, Any?> {
        return (obj::class as KClass<T>).memberProperties.associate { prop ->
            prop.name to prop.get(obj)?.let { value ->
                if (value::class.isData) {
                    toMap(value)
                } else {
                    value
                }
            }
        }
    }

    fun flattenSkipNull(map: Map<String, Any?>, path: String): Map<String, String> {
        val toReturn = mutableMapOf<String, String>()
        map.forEach { (k, v) ->
            if (v is Map<*, *>) {
                val map = flattenSkipNull(v as Map<String, Any?>, path + "_" + k)
                toReturn.putAll(map)
            } else {
                v?.run { toReturn.put("${path}_$k".lowercase(Locale.getDefault()), v.toString()) }
            }
        }
        return toReturn
    }

}
