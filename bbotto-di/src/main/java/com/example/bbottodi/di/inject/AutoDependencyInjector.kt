package com.example.bbottodi.di.inject

import com.example.bbottodi.di.Container
import com.example.bbottodi.di.annotation.Inject
import com.example.bbottodi.di.annotation.Qualifier
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

//    fun <T : Any> injectLegacy(container: Container, clazz: KClass<*>): T {
//        var instance = container.getInstanceLegacy(clazz, clazz.annotations)
//        if (instance == null) instance = createInstanceLegacy(container, clazz)
//        return instance as T
//    }
//
//    private fun <T : Any> createInstanceLegacy(container: Container, clazz: KClass<*>): T {
//        val constructor = clazz.primaryConstructor
//            ?: throw IllegalArgumentException(INJECT_ERROR_MESSAGE)
//        val parameters = constructor.parameters.filter { parameter ->
//            parameter.hasAnnotation<Inject>()
//        }
//        val arguments = parameters.map { parameter ->
//            val type = parameter.type.jvmErasure
//            container.getInstanceLegacy(type, parameter.annotations) ?: injectLegacy(
//                container,
//                type,
//            )
//                ?: throw IllegalStateException(NONE_INSTANCE_ERROR_MESSAGE)
//        }
//        val instance = constructor.call(*arguments.toTypedArray()) as T
//        injectFieldLegacy(container, instance)
//        return instance
//    }
//
//    private fun <T : Any> injectFieldLegacy(container: Container, instance: T) {
//        val properties = instance::class.declaredMemberProperties.filter { property ->
//            property.hasAnnotation<Inject>()
//        }
//        properties.forEach { property ->
//            property.isAccessible = true
//            property.javaField?.let {
//                val type = it.type.kotlin
//                val fieldValue = container.getInstanceLegacy(type, it.annotations.toList())
//                    ?: throw IllegalStateException(NONE_INSTANCE_ERROR_MESSAGE)
//                it.set(instance, fieldValue)
//            }
//        }
//    }

    fun <T : Any> inject(container: Container, clazz: KClass<*>): T {
        val annotation =
            clazz.annotations.firstOrNull { it.annotationClass.hasAnnotation<Qualifier>() }
        var instance = container.getInstance(clazz, annotation)
        if (instance == null) {
            val annotation = clazz.annotations.firstOrNull {
                it.annotationClass.hasAnnotation<Qualifier>()
            }
            instance = container.getInstance(clazz, annotation)
        } // createInstance(container, clazz)
        return instance as T
    }

    fun <T : Any> injectField(container: Container, instance: T) {
        val properties = instance::class.declaredMemberProperties.filter { property ->
            property.hasAnnotation<Inject>()
        }
        properties.forEach { property ->
            property.isAccessible = true
            val type = property.returnType.jvmErasure
            val annotation =
                property.annotations.firstOrNull { it.annotationClass.hasAnnotation<Qualifier>() }
            val argument = container.getInstance(type, annotation)
            property.javaField?.set(instance, argument)
        }
    }

    fun <T : Any> createInstance(container: Container, clazz: KClass<*>): T {
        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("[ERROR] 인자가 없어")
        val parameters = constructor.parameters.filter { parameter ->
            parameter.hasAnnotation<Inject>()
        }
        val arguments = parameters.map { parameter ->
            val type = parameter.type.jvmErasure
            val annotation =
                parameter.annotations.firstOrNull { it.annotationClass.hasAnnotation<Qualifier>() }
            container.getInstance(type, annotation)
                ?: inject(container, type)
                ?: throw IllegalStateException()
        }
        val instance = constructor.call(*arguments.toTypedArray()) as T
        injectField(container, instance)
        return instance
    }

    // 다시 새로 로직 짜기
}
