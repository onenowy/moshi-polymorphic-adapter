package dev.onenowy.moshipolymorphicadapter.sealed.codegen

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import dev.onenowy.moshipolymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.Companion.OPTION_GENERATE_PROGUARD_RULES
import org.junit.Test
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties

class KotlinSealedCodegenProcessorTest {
    private fun generateResult(source: SourceFile, proguard: Boolean = true) = KotlinCompilation().apply {
        sources = listOf(source)
        annotationProcessors =
            listOf(KotlinSealedCodegenProcessor())
        inheritClassPath = true
        kaptArgs[OPTION_GENERATE_PROGUARD_RULES] = proguard.toString()
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
        val adapterProperty = adapterClass.declaredMemberProperties.find { it.name == "polymorphicAdapter" }
        assertThat(adapterProperty.toString()).contains("JsonAdapter")
        assertThat(adapterProperty.toString()).contains(typeName)
        val fromJson = adapterClass.declaredFunctions.find { it.name == "fromJson" }
        assertThat(fromJson?.returnType.toString()).contains(typeName)
        val toJson = adapterClass.declaredFunctions.find { it.name == "toJson" }
        assertThat(toJson?.parameters?.find { it.name?.contains("value") ?: false }.toString()).contains(typeName)
        proguardTest(source, typeName, adapterName, result)
    }

    private fun proguardTest(
        source: SourceFile,
        typeName: String,
        adapterName: String,
        result: KotlinCompilation.Result
    ) {
        assertThat(result.generatedFiles.find { it.name.contains("moshi-sealed-polymorphic-$typeName") }
            ?.readText()).contains(
            """
          -if class $typeName
          -keepnames class $typeName
          -if class $typeName
          -keep class $adapterName {
              public <init>(com.squareup.moshi.Moshi);
          }
          """.trimIndent()
        )
        val withoutProguardResult = generateResult(source, false)
        assertThat(withoutProguardResult.generatedFiles.find { it.name.contains("moshi-sealed-polymorphic-$typeName") }).isNull()
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
            import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import dev.onenowy.moshipolymorphicadapter.annotations.DefaultNull
            import dev.onenowy.moshipolymorphicadapter.annotations.NameLabel
            import com.squareup.moshi.JsonClass
            import com.squareup.moshi.Json

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
            @DefaultNull
            sealed class Computer

            @JsonClass(generateAdapter = true)
            @NameLabel("monitor_Unique")
            data class Monitor(@Json(name = "monitor_Unique") val monitorUnique: Int?, val testValue: String) : Computer()

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
            import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import dev.onenowy.moshipolymorphicadapter.annotations.ValueLabel
            import com.squareup.moshi.JsonClass

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
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
            import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
            import dev.onenowy.moshipolymorphicadapter.annotations.ValueLabel
            import com.squareup.moshi.JsonClass

            @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
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