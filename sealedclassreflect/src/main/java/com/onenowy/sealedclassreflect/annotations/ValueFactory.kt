package com.onenowy.sealedclassreflect.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueFactory(val labelType: KClass<out Any>, val labelKey: String)
