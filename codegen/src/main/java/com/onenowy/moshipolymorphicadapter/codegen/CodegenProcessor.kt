package com.onenowy.moshipolymorphicadapter.codegen

import com.google.auto.service.AutoService
import com.onenowy.moshipolymorphicadapter.codegen.annotations.CodegenNameAdapterFactory
import com.onenowy.moshipolymorphicadapter.codegen.annotations.CodegenValueAdaterFactory
import com.onenowy.moshipolymorphicadapter.codegen.api.NameAdapterFactoryCodeGenerator
import com.onenowy.moshipolymorphicadapter.codegen.api.ValueAdapterFactoryCodeGenerator
import com.onenowy.moshipolymorphicadapter.codegen.api.adapterFactoryGenerator
import com.onenowy.moshipolymorphicadapter.codegen.api.toTargetSealedClass
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassInspector
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@KotlinPoetMetadataPreview
class CodegenProcessor : AbstractProcessor() {

    private lateinit var types: Types
    private lateinit var elements: Elements
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var classInspector: ClassInspector
    private val nameAdapterFactoryAnnotation = CodegenNameAdapterFactory::class.java
    private val valueAdapterFactoryAnnotation = CodegenValueAdaterFactory::class.java
    private val postfixString = "AdapterFactory"

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        elements = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
        classInspector = ElementsClassInspector.create(elements, types)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            return false
        }
        for (type in roundEnv.getElementsAnnotatedWith(nameAdapterFactoryAnnotation)) {
            type.toTargetSealedClass(messager, types)?.run {
                adapterFactoryGenerator(NameAdapterFactoryCodeGenerator(this), "Name$postfixString", elements).writeTo(filer)
            }
        }
        for (type in roundEnv.getElementsAnnotatedWith(valueAdapterFactoryAnnotation)) {
            type.toTargetSealedClass(messager, types)?.run {
                adapterFactoryGenerator(
                    ValueAdapterFactoryCodeGenerator(this, type.getAnnotation(valueAdapterFactoryAnnotation)),
                    "value$postfixString",
                    elements
                ).writeTo(filer)
            }
        }
        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes() = setOf(nameAdapterFactoryAnnotation.canonicalName, valueAdapterFactoryAnnotation.canonicalName)

}