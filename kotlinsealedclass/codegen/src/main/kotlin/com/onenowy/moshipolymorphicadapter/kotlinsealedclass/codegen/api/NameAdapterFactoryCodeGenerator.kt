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

import com.onenowy.moshipolymorphicadapter.NamePolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toKmClass


class NameAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass) :
    AbstractAdapterFactoryCodeGenerator(targetSealedClass) {
    private val annotation = NameLabel::class.java
    override fun generateCode(): CodeBlock {
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java)",
                NamePolymorphicAdapterFactory::class,
                targetSealedClass.baseType.toKmClass().toClassName()
            )
            for (type in targetSealedClass.subClass) {
                val labelField = type.getAnnotation(annotation)
                addStatement(
                    "adapterFactory = adapterFactory.withSubtype(%T::class.java, %S)",
                    type.toKmClass().toClassName(),
                    labelField.name
                )
            }
            addStatement("return adapterFactory")
        }
    }
}