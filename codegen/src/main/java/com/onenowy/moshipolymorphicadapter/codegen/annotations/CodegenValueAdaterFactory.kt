package com.onenowy.moshipolymorphicadapter.codegen.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class CodegenValueAdaterFactory(val labelType: KClass<out Any>, val labelKey: String)