package com.onenowy.moshipolymorphicadapter.kotlinsealed.codegen

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.junit.Test
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties

class KotlinSealedCodegenProcessorTest {
    private fun generateResult(source: SourceFile) = KotlinCompilation().apply {
        sources = listOf(source)
        annotationProcessors = listOf(KotlinSealedCodegenProcessor())
        inheritClassPath = true
        messageOutputStream = System.out
    }.compile()

    private fun testTemplate(source: SourceFile, typeName: String, defaultValue: Boolean = false) {
        val adapterName = "${typeName}JsonAdapter"
        val result = generateResult(source)
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val adapterFile = result.sourcesGeneratedByAnnotationProcessor.first { it.name == "$adapterName.kt" }
        assertThat(adapterFile.exists()).isTrue()
        val codeText = adapterFile.readText()
        println(codeText)
        assertThat(codeText).contains(
            "@Suppress(\"DEPRECATION\", \"unused\", \"ClassName\", \"REDUNDANT_PROJECTION\", \"RedundantExplicitType\",\n" +
                    "    \"LocalVariableName\", \"RedundantVisibilityModifier\")"
        )
        assertThat(codeText).contains("@Suppress(\"UNCHECKED_CAST\")")
        if (defaultValue) {
            assertThat(codeText).contains("withDefaultValue(null)")
        }
        val adapterClass = result.classLoader.loadClass(adapterName).kotlin
        val adapterProperty = adapterClass.declaredMemberProperties.first { it.name == "polymorphicAdapter" }
        assertThat(adapterProperty.toString()).contains("JsonAdapter")
        assertThat(adapterProperty.toString()).contains(typeName)
        val fromJson = adapterClass.declaredFunctions.first { it.name == "fromJson" }
        assertThat(fromJson.returnType.toString()).contains(typeName)
        val toJson = adapterClass.declaredFunctions.first { it.name == "toJson" }
        assertThat(toJson.parameters.first { it.name?.contains("value") ?: false }.toString()).contains(typeName)
    }

    @Suppress("SameParameterValue")
    private fun errorTestTemplate(source: SourceFile, errorMessage: String) {
        val result = generateResult(source)
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains(errorMessage)
    }


    @Test
    fun nameAdapterGenerationTest() {
        val typeName = "Computer"
        val source = kotlin(
            "$typeName.kt", """
            import com.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import com.onenowy.moshipolymorphicadapter.annotations.DefaultNull
            import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
            import com.squareup.moshi.JsonClass

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
            @DefaultNull
            sealed class Computer

            @JsonClass(generateAdapter = true)
            @NameLabel("monitorUnique")
            data class Monitor(val monitorUnique: Int?, val testValue: String) : Computer()

            @JsonClass(generateAdapter = true)
            @NameLabel("mouseUnique")
            data class Mouse(val mouseUnique: String?, val testValue: String) : Computer()

            @JsonClass(generateAdapter = true)
            @NameLabel("keyboardUnique")
            data class Keyboard(val keyboardUnique: Boolean?, val testValue: String) : Computer()
        """.trimIndent()
        )
        testTemplate(source, typeName, true)
    }

    @Test
    fun valueAdapterGeneratorTest() {
        val typeName = "ComputerValue"
        val source = kotlin(
            "$typeName.kt",
            """
            import com.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
            import com.squareup.moshi.JsonClass

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_ADAPTER.INT + ":type")
            sealed class ComputerValue

            @ValueLabel(1.toString())
            @JsonClass(generateAdapter = true)
            data class MonitorValue(val monitor: Int?, val testValue: String) :ComputerValue()

            @ValueLabel(2.toString())
            @JsonClass(generateAdapter = true)
            data class MouseValue(val mouse: String?, val testValue: String) : ComputerValue()

            @ValueLabel(3.toString())
            @JsonClass(generateAdapter = true)
            data class KeyboardValue(val keyboard: Boolean?, val testValue: String) : ComputerValue()
        """.trimIndent()
        )
        testTemplate(source, typeName)
    }

    @Test
    fun notSealed() {
        val source = kotlin(
            "NotSealedComputerValue.kt", """
            import com.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
            import com.squareup.moshi.JsonClass

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_ADAPTER.INT + ":type")
            interface NotSealedComputerValue

            @ValueLabel(1.toString())
            @JsonClass(generateAdapter = true)
            data class MonitorNotSealedValue(val monitor: Int?, val testValue: String) : NotSealedComputerValue

            @ValueLabel(2.toString())
            @JsonClass(generateAdapter = true)
            data class MouseNotSealedValue(val mouse: String?, val testValue: String) : NotSealedComputerValue
        """.trimIndent()
        )
        errorTestTemplate(source, "Must be a sealed class!")
    }
}