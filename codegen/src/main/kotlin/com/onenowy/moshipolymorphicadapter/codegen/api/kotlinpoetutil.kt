package com.onenowy.moshipolymorphicadapter.codegen.api

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass

fun ImmutableKmClass.toClassName(): ClassName {
    val names = this.name.split("/")
    var pakage = names[0]
    for (i in 1 until names.lastIndex) {
        pakage += "." + names[i]
    }
    return ClassName(pakage, names.last())
}