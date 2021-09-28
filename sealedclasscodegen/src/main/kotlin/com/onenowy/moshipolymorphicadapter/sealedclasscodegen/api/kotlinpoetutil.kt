package com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api

import com.squareup.kotlinpoet.ClassName
import kotlinx.metadata.KmClass


fun KmClass.toClassName(): ClassName {
    val names = this.name.split("/")
    val sb = StringBuffer(names[0])
    for (i in 1 until names.lastIndex) {
        sb.append(".${names[i]}")
    }
    return ClassName(sb.toString(), names.last())
}