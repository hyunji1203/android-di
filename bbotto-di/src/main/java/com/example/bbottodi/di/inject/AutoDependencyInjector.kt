package com.example.bbottodi.di.inject

import com.example.bbottodi.di.Container
import com.example.bbottodi.di.annotation.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

object AutoDependencyInjector {
    private const val INJECT_ERROR_MESSAGE = "주입 할 생성자가 존재 하지 않습니다."
    private const val NONE_INSTANCE_ERROR_MESSAGE = "해당 인스턴스가 존재하지 않습니다."

    fun <T : Any> inject(clazz: KClass<*>): T {
        var instance = Container.getInstance(clazz, clazz.annotations)
        if (instance == null) instance = createInstance(clazz)
        return instance as T
    }

    private fun <T : Any> createInstance(clazz: KClass<*>): T {
        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException(INJECT_ERROR_MESSAGE)
        val parameters = constructor.parameters.filter { parameter ->
            parameter.hasAnnotation<Inject>()
        }
        val arguments = parameters.map { parameter ->
            val type = parameter.type.jvmErasure
            Container.getInstance(type, parameter.annotations) ?: inject(type)
                ?: throw IllegalStateException(NONE_INSTANCE_ERROR_MESSAGE)
        }
        val instance = constructor.call(*arguments.toTypedArray()) as T
        injectField(instance)
        return instance
    }

    private fun <T : Any> injectField(instance: T) {
        val properties = instance::class.declaredMemberProperties.filter { property ->
            property.hasAnnotation<Inject>()
        }
        properties.forEach { property ->
            property.isAccessible = true
            property.javaField?.let {
                val type = it.type.kotlin
                val fieldValue = Container.getInstance(type, it.annotations.toList())
                    ?: throw IllegalStateException(NONE_INSTANCE_ERROR_MESSAGE)
                it.set(instance, fieldValue)
            }
        }
    }
}
