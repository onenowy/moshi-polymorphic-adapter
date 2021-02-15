package com.onenowy.moshipolymorphicadapter.codegen.api

import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types
import javax.tools.Diagnostic

data class TargetSealedClass(val baseType: TypeElement, val subClass: List<TypeElement>)

@KotlinPoetMetadataPreview
fun Element.toTargetSealedClass(messager: Messager, typeUtil: Types, annotatedSubclass: Set<Element>): TargetSealedClass? {
    if (this is TypeElement) {
        val kmClass: ImmutableKmClass? = try {
            this.toImmutableKmClass()
        } catch (e: UnsupportedOperationException) {
            null
        }
        if (kmClass?.isSealed == true) {
            val subclass = annotatedSubclass.filter { it is TypeElement && typeUtil.directSupertypes(it.asType()).contains(this.asType()) }.map { it as TypeElement }
            return TargetSealedClass(this, subclass)
        }
    }
    messager.printMessage(Diagnostic.Kind.ERROR, "Error in Annotation Processing : $this is not sealed class", this)
    return null
}
