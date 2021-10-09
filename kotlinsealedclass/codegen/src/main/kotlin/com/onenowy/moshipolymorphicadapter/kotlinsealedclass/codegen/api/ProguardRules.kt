package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api

import com.squareup.kotlinpoet.ClassName
import okio.use
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.tools.StandardLocation

internal data class ProguardRules(
    val targetClass: ClassName,
    val adapterName: String,
    val adapterConstructorParams: List<String>
) {
    private val outputFile = "META-INF/proguard/moshi-kotlinsealed-polymorphic-${targetClass.canonicalName}.pro"

    fun writeTo(filer: Filer, vararg originatingElements: Element) {
        filer.createResource(StandardLocation.CLASS_OUTPUT, "", outputFile, *originatingElements)
            .openWriter()
            .use(::writeTo)
    }

    private fun writeTo(out: Appendable): Unit = out.run {
        //
        // -if class {the target class}
        // -keepnames class {the target class}
        // -if class {the target class}
        // -keep class {the generated adapter} {
        //    <init>(...);
        // }
        //
        val targetName = targetClass.reflectionName()
        val adapterCanonicalName = ClassName(targetClass.packageName, adapterName).canonicalName
        // Keep the class name for Moshi's reflective lookup based on it
        appendLine("-if class $targetName")
        appendLine("-keepnames class $targetName")

        appendLine("-if class $targetName")
        appendLine("-keep class $adapterCanonicalName {")
        // Keep the constructor for Moshi's reflective lookup
        val constructorArgs = adapterConstructorParams.joinToString(",")
        appendLine("    public <init>($constructorArgs);")
        appendLine("}")
    }
}
