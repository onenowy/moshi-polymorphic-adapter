package com.onenowy.moshipolymorphicadapter.sealedclasscodegen

import com.google.auto.service.AutoService
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.NameLabel
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.NamePolymorphicAdapter
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValuePolymorphicAdapter
import com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api.NameAdapterFactoryCodeGenerator
import com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api.ValueAdapterFactoryCodeGenerator
import com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api.adapterFactoryGenerator
import com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api.toTargetSealedClass
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class CodegenProcessor : AbstractProcessor() {

    private lateinit var types: Types
    private lateinit var elements: Elements
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private val namePolymorphicAdapterAnnotation = NamePolymorphicAdapter::class.java
    private val valuePolymorphicAdapterAnnotation = ValuePolymorphicAdapter::class.java
    private val nameLabelAnnotation = NameLabel::class.java
    private val valueLabelAnnotation = ValueLabel::class.java
    private val postfixString = "AdapterFactory"

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        elements = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            return false
        }
        val nameLabelElements = roundEnv.getElementsAnnotatedWith(nameLabelAnnotation)
        val valueLabelElements = roundEnv.getElementsAnnotatedWith(valueLabelAnnotation)
        for (type in roundEnv.getElementsAnnotatedWith(namePolymorphicAdapterAnnotation)) {
            type.toTargetSealedClass(messager, types, nameLabelElements)?.run {
                adapterFactoryGenerator(NameAdapterFactoryCodeGenerator(this), "Name$postfixString", elements).writeTo(
                    filer
                )
            }
        }
        for (type in roundEnv.getElementsAnnotatedWith(valuePolymorphicAdapterAnnotation)) {
            type.toTargetSealedClass(messager, types, valueLabelElements)?.run {
                adapterFactoryGenerator(
                    ValueAdapterFactoryCodeGenerator(this, type.getAnnotation(valuePolymorphicAdapterAnnotation)),
                    "Value$postfixString",
                    elements
                ).writeTo(filer)

            }
        }
        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes() = setOf(
        namePolymorphicAdapterAnnotation.canonicalName,
        valuePolymorphicAdapterAnnotation.canonicalName,
        nameLabelAnnotation.canonicalName,
        valueLabelAnnotation.canonicalName
    )

}