package com.example.bbottodi.di

import com.example.bbottodi.di.model.InstanceIdentifier
import kotlin.reflect.KClass

object Container {
    private val instances = mutableMapOf<InstanceIdentifier, Any>()

    fun addInstance(type: KClass<*>, instance: Any) {
        val annotations = instance::class.annotations.map { it::class.simpleName!! }
        val key = InstanceIdentifier(type, annotations)
        instances[key] = instance
    }

    fun addInstance(type: KClass<*>, annotations: List<String>, instance: Any) {
        val key = InstanceIdentifier(type, annotations)
        instances[key] = instance
    }

    fun getInstance(clazz: KClass<*>, annotations: List<Annotation>): Any? {
        val key = InstanceIdentifier(clazz, annotations.map { it.annotationClass.simpleName ?: "" })
        val instanceKey = instances.keys.firstOrNull {
            key.type == it.type && key.qualifier.containsAll(it.qualifier)
        } ?: return null
        return instances[instanceKey]
    }
}
