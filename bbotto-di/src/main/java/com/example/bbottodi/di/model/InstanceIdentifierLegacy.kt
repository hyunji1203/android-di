package com.example.bbottodi.di.model

import kotlin.reflect.KClass

data class InstanceIdentifierLegacy(
    val type: KClass<*>,
    val qualifier: List<String>,
)

data class InstanceIdentifier(
    val type: KClass<*>,
    val qualifier: Annotation?,
)
