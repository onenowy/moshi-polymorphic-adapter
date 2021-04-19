package com.onenowy.moshipolymorphicadapter.codegen.api

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass

fun ImmutableKmClass.toClassName(): ClassName {
    val names = this.name.split("/")
    val sb = StringBuffer(names[0])
    for (i in 1 until names.lastIndex) {
        sb.append(".$names[i]")
    }
    return ClassName(sb.toString(), names.last())
}