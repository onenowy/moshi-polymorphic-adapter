package dev.onenowy.moshipolymorphicadapter.sealed.codegen

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toKmClass
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import dev.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import dev.onenowy.moshipolymorphicadapter.annotations.NameLabel
import dev.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import dev.onenowy.moshipolymorphicadapter.sealed.codegen.api.preparedAdapter
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

        private val SUPPRESS_NAMES = arrayOf(
            // https://github.com/square/moshi/issues/1023
            "DEPRECATION",
            // Because we look it up reflectively
            "unused",
            // Because we include underscores
            "ClassName",
            // Because we generate redundant `out` variance for some generics and there's no way
            // for us to know when it's redundant.
            "REDUNDANT_PROJECTION",
            // Because we may generate redundant explicit types for local vars with default values.
            // Example: 'var fooSet: Boolean = false'
            "RedundantExplicitType",
            // NameAllocator will just add underscores to differentiate names, which Kotlin doesn't
            // like for stylistic reasons.
            "LocalVariableName",
            // KotlinPoet always generates explicit public modifiers for public members.
            "RedundantVisibilityModifier"
        )
        internal val COMMON_SUPPRESS =
            SUPPRESS_NAMES.let { suppression ->
                AnnotationSpec.builder(Suppress::class)
                    .addMember(
                        suppression.indices.joinToString { "%S" },
                        *suppression
                    )
                    .build()
            }
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        elements = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
        generatedAnnotation =
            processingEnv.options[OPTION_GENERATED]?.let {
                POSSIBLE_GENERATED_NAMES[it]
                    ?: error(
                        "Invalid option value for $OPTION_GENERATED. Found $it, " +
                                "allowable values are $POSSIBLE_GENERATED_NAMES."
                    )
            }
        generateProguardRules =
            processingEnv.options[OPTION_GENERATE_PROGUARD_RULES]?.toBooleanStrictOrNull()
                ?: true
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            return false
        }
        for (element in roundEnv.getElementsAnnotatedWith(jsonClassAnnotation)) {
            val jsonClass =
                element.getAnnotation(jsonClassAnnotation)
            if (element is TypeElement && jsonClass.generator.startsWith("MoshiPolymorphic")) {
                val kmClass =
                    element.getAnnotation(metadataAnnotation)
                        .toKmClass()
                if (!kmClass.flags.isSealed) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Must be a sealed class!", element)
                    return false
                }
                val sealedSubClass = kmClass.sealedSubclasses.map {               // Canonicalize
                    it.replace("/", ".")
                }.map { elements.getTypeElement(it) }
                preparedAdapter(
                    element,
                    kmClass,
                    sealedSubClass,
                    jsonClass.generator.split(":"),
                    messager,
                    generatedAnnotation,
                    generateProguardRules
                )?.apply {
                    this.fileSpec.writeTo(filer)
                    this.proguardRules?.writeTo(filer, element)
                }
            }

        }
        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = setOf(
        jsonClassAnnotation.canonicalName,
        nameLabelAnnotation.canonicalName,
        valueLabelAnnotation.canonicalName,
        defaultNullAnnotation.canonicalName
    )

    override fun getSupportedOptions(): Set<String> =
        setOf(OPTION_GENERATED)
}