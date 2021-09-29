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

import com.onenowy.moshipolymorphicadapter.ValuePolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.annotations.ValuePolymorphicAdapter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toKmClass

class ValueAdapterFactoryCodeGenerator(
    targetSealedClass: TargetSealedClass,
    private val valueAdapterFactoryCodegen: ValuePolymorphicAdapter
) :
    AbstractAdapterFactoryCodeGenerator
        (targetSealedClass) {
    private val annotation = ValueLabel::class.java
    override fun generateCode(): CodeBlock {
        val labelType = valueAdapterFactoryCodegen.labelType
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java, %S, %T.%L, %L)",
                ValuePolymorphicAdapterFactory::class,
                targetSealedClass.baseType.toKmClass().toClassName(),
                valueAdapterFactoryCodegen.labelKey,
                labelType::class,
                labelType,
                valueAdapterFactoryCodegen.subTypeIncludeLabelKey
            )
            for (type in targetSealedClass.subClass) {
                val labelValue = type.getAnnotation(annotation)
                addStatement(
                    "adapterFactory = adapterFactory.withSubtypeForLabelString(%T::class.java, %S)",
                    type.toKmClass().toClassName(),
                    labelValue.value
                )
            }
            addStatement("return adapterFactory")
        }
    }
}