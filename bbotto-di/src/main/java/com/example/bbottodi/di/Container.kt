package com.example.bbottodi.di

import com.example.bbottodi.di.annotation.Qualifier
import com.example.bbottodi.di.model.InstanceIdentifier
import com.example.bbottodi.di.model.InstanceIdentifierLegacy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

open class Container(
    private val parentContainer: Container? = null,
    private val module: Module,
) {
    private val instancesLegacy = mutableMapOf<InstanceIdentifierLegacy, Any>()
    fun addInstanceLegacy(type: KClass<*>, instance: Any) {
        val annotations =
            if (instance is KFunction<*>) {
                listOf("Inject")
            } else {
                instance::class.annotations.map { it::class.simpleName!! }
            }
        val key = InstanceIdentifierLegacy(type, annotations)
        instancesLegacy[key] = instance
    }

    fun addInstanceLegacy(type: KClass<*>, annotations: List<String>, instance: Any) {
        val key = InstanceIdentifierLegacy(type, annotations)
        instancesLegacy[key] = instance
    }

    fun getInstanceLegacy(clazz: KClass<*>, annotations: List<Annotation>): Any? {
        val key =
            InstanceIdentifierLegacy(clazz, annotations.map { it.annotationClass.simpleName ?: "" })
        val instanceKey = instancesLegacy.keys.firstOrNull {
            key.type == it.type && key.qualifier.containsAll(it.qualifier)
        } ?: return parentContainer?.getInstanceLegacy(clazz, annotations)
        val instance = instancesLegacy[instanceKey]
        if (instance is KFunction<*>) {
            return instance.call()
        }
        return instance
    }

    private val instances = mutableMapOf<InstanceIdentifier, Any>()

    fun addInstance(module: Module) {
        val methods = module::class.declaredFunctions
        methods.forEach { method ->
            val type = method.returnType.jvmErasure
            val qualifier =
                method.annotations.firstOrNull { it.annotationClass.hasAnnotation<Qualifier>() }
            val key = InstanceIdentifier(type, qualifier)
            instances[key] = addInstance(method)
        }
    }

    fun addInstance(kFunction: KFunction<*>): Any {
        val args = kFunction.parameters.map { parameter ->
            getInstance(
                parameter.type.jvmErasure,
                parameter.annotations.firstOrNull { it.annotationClass.hasAnnotation<Qualifier>() },
            )
        }
        return kFunction.call(*args.toTypedArray())!!
    }

    fun getInstance(clazz: KClass<*>, annotation: Annotation?): Any? {
        val key = InstanceIdentifier(clazz, annotation)
        val function = module::class.declaredFunctions.firstOrNull {
            key.type == it.returnType && key.qualifier == it.annotations.firstOrNull { annotation ->
                annotation.annotationClass.hasAnnotation<Qualifier>()
            }
        }
        return if (function != null) {
            val instanceKey = instances.keys.firstOrNull {
                key.type == it.type && key.qualifier == it.qualifier
            } ?: {
                val instance = getInstance(function)
                instances[key] = instance
            }
            instances[instanceKey]
        } else {
            parentContainer?.getInstance(clazz, annotation)
        }
    }

    private fun getInstance(kFunction: KFunction<*>): Any {
        val args = kFunction.parameters.map { parameter ->
            getInstance(
                parameter.type.jvmErasure,
                parameter.annotations.firstOrNull { annotation ->
                    annotation.annotationClass.hasAnnotation<Qualifier>()
                },
            )
        }
        return kFunction.call(*args.toTypedArray())!!
    }
}
