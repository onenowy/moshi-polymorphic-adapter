package com.onenowy.moshipolymorphicadapter.codegen.api

import com.squareup.kotlinpoet.CodeBlock

abstract class AbstractAdapterFactoryCodeGenerator(val targetSealedClass: TargetSealedClass) {
    abstract fun generateCode(): CodeBlock

} 