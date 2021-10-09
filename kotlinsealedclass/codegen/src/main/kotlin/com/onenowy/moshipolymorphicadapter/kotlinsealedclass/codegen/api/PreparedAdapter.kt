package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api

import com.squareup.kotlinpoet.FileSpec

internal data class PreparedAdapter(val fileSpec: FileSpec, val proguardRules: ProguardRules?)

