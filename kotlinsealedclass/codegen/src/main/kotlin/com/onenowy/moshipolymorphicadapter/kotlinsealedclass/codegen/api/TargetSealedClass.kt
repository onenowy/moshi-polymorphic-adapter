/*
 * Copyright (c) 2021 nowy(nowy08 at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api

import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toKmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types
import javax.tools.Diagnostic

data class TargetSealedClass(val baseType: TypeElement, val subClass: List<TypeElement>)

fun Element.toTargetSealedClass(messager: Messager, typeUtil: Types, annotatedSubclass: Set<Element>): TargetSealedClass? {
    if (this is TypeElement) {
        val kmClass = try {
            this.toKmClass()
        } catch (e: UnsupportedOperationException) {
            null
        }
        if (kmClass?.flags?.isSealed == true) {
            val subclass = annotatedSubclass.filter {
                it is TypeElement && typeUtil.directSupertypes(it.asType()).contains(this.asType())
            }.map { it as TypeElement }
            return TargetSealedClass(this, subclass)
        }
    }
    messager.printMessage(Diagnostic.Kind.ERROR, "Error in Annotation Processing : $this is not sealed class", this)
    return null
}
