package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen

import com.google.auto.service.AutoService
import com.onenowy.moshipolymorphicadapter.AdapterType
import com.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api.PreparedAdapter
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api.ProguardRules
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api.adapterClassSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toKmClass
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.metadata.KmClass
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class KotlinSealedCodegenProcessor : AbstractProcessor() {

    private lateinit var types: Types
    private lateinit var elements: Elements
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private var generatedAnnotation: ClassName? = null
    private var generateProguardRules: Boolean = true

    companion object {
        internal val metadataAnnotation = Metadata::class.java
        internal val jsonClassAnnotation = JsonClass::class.java
        internal val nameLabelAnnotation = NameLabel::class.java
        internal val valueLabelAnnotation = ValueLabel::class.java
        internal val defaultNullAnnotation = DefaultNull::class.java
        internal val moshiClass = Moshi::class

        /**
         * Moshi's options are used.
         *
         * This annotation processing argument can be specified to have a `@Generated` annotation
         * included in the generated code. It is not encouraged unless you need it for static analysis
         * reasons and not enabled by default.
         *
         * Note that this can only be one of the following values:
         *   * `"javax.annotation.processing.Generated"` (JRE 9+)
         *   * `"javax.annotation.Generated"` (JRE <9)        *
         *
         */
        const val OPTION_GENERATED: String = "moshi.generated"

        /**
         * This boolean processing option can control proguard rule generation.
         * Normally, this is not recommended unless end-users build their own JsonAdapter look-up tool.
         * This is enabled by default.
         */
        const val OPTION_GENERATE_PROGUARD_RULES: String = "moshi.generateProguardRules"

        private val POSSIBLE_GENERATED_NAMES = arrayOf(
            ClassName("javax.annotation.processing", "Generated"),
            ClassName("javax.annotation", "Generated")
        ).associateBy { it.canonicalName }
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        elements = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
        generatedAnnotation = processingEnv.options[OPTION_GENERATED]?.let {
            POSSIBLE_GENERATED_NAMES[it] ?: error(
                "Invalid option value for $OPTION_GENERATED. Found $it, " +
                        "allowable values are $POSSIBLE_GENERATED_NAMES."
            )
        }
        generateProguardRules = processingEnv.options[OPTION_GENERATE_PROGUARD_RULES]?.toBooleanStrictOrNull() ?: true
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            return false
        }
        for (element in roundEnv.getElementsAnnotatedWith(jsonClassAnnotation)) {
            val jsonClass = element.getAnnotation(jsonClassAnnotation)
            if (element is TypeElement && jsonClass.generator.startsWith(AdapterType.PREFIX)) {
                val kmClass = element.getAnnotation(metadataAnnotation).toKmClass()
                if (!kmClass.flags.isSealed) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Must be a sealed class!", element)
                    return false
                }
                val sealedSubClass = kmClass.sealedSubclasses.map {               // Canonicalize
                    it.replace("/", ".")
                }.map { elements.getTypeElement(it) }
                preparedAdapter(element, kmClass, sealedSubClass, jsonClass.generator.split(":"))?.apply {
                    this.fileSpec.writeTo(filer)
                    this.proguardRules?.writeTo(filer, element)
                }
            }

        }
        return false
    }


    @OptIn(DelicateKotlinPoetApi::class)
    private fun preparedAdapter(
        element: TypeElement,
        kmClass: KmClass,
        sealedSubClasses: List<TypeElement>,
        generatorTag: List<String>
    ): PreparedAdapter? {

        for (subclass in sealedSubClasses) {
            if (subclass.typeParameters.isNotEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Moshi-sealed subtypes cannot be generic.", subclass)
                return null
            }
        }
        val adapterName = ClassName.bestGuess(
            com.squareup.moshi.Types.generatedJsonAdapterName(
                element.asClassName().reflectionName()
            )
        ).simpleName
        val targetType = element.asClassName()
        val typeSpec = adapterClassSpec(
            element,
            kmClass,
            adapterName,
            targetType,
            generatorTag,
            sealedSubClasses,
            messager,
            generatedAnnotation
        )
        typeSpec ?: return null

        val fileSpec = FileSpec.builder(targetType.packageName, adapterName)
            .addType(typeSpec)
            .build()

        val proguardRules = if (generateProguardRules) {
            ProguardRules(
                targetClass = targetType,
                adapterName = adapterName,
                adapterConstructorParams = listOf(moshiClass.asClassName().reflectionName())
            )
        } else {
            null
        }

        return PreparedAdapter(fileSpec, proguardRules)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = setOf(
        jsonClassAnnotation.canonicalName,
        nameLabelAnnotation.canonicalName,
        valueLabelAnnotation.canonicalName,
        defaultNullAnnotation.canonicalName
    )

    override fun getSupportedOptions(): Set<String> = setOf(OPTION_GENERATED)
}