package com.idba.compiler.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.idba.annotation.inject.InjectParams;
import com.idba.compiler.utils.TypeTools;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.ElementKind.CLASS;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-17 10:57
 * describe: APT-compile files
 **/
@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor{

    private Filer filer;
    private Elements elementUtils;
    private TypeTools typeTools;

    private static final List<Class<InjectParams>> ANNOTATIONS = Arrays.asList(
            InjectParams.class
    );



    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        this.filer = processingEnvironment.getFiler();
        Types typeUtils = processingEnvironment.getTypeUtils();
        this.elementUtils = processingEnvironment.getElementUtils();

        this.typeTools = new TypeTools(typeUtils, elementUtils);
    }





    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //����ע��
        Map<TypeElement, TargetClass> targetClassMap = findAndParseTargets(roundEnvironment);

        //������ɺ����ɵĴ���Ľṹ�Ѿ����ˣ����Ǵ���InjectingClass��
        for (Map.Entry<TypeElement, TargetClass> entry : targetClassMap.entrySet()) {

            TypeElement typeElement = entry.getKey();
            TargetClass targetClass = entry.getValue();

            JavaFile javaFile = targetClass.brewJava();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error(typeElement, "Unable to write injecting for type %s: %s", typeElement, e.getMessage());
            }
        }
        return false;
    }


    /**
     * ���ֲ�����ע���ֶ�
     */
    private Map<TypeElement, TargetClass> findAndParseTargets(RoundEnvironment roundEnvironment) {

        Map<TypeElement, TargetClass> targetClassMap = new LinkedHashMap<>();

        for (Class<? extends Annotation> clazz : ANNOTATIONS) {
            //process each @InjectIntentExtrasParam/@InjectUriParam element.
            for (Element element : roundEnvironment.getElementsAnnotatedWith(clazz)) {
                if (!SuperficialValidation.validateElement(element)) continue;
                parseInjectParam(element, targetClassMap, clazz);
            }
        }
        return targetClassMap;
    }

    /**
     * ������ @InjectParams ע����ֶ�
     */
    private void parseInjectParam(Element element, Map<TypeElement, TargetClass> targetClassMap, Class<? extends Annotation> clazz) {

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        //���ȶԱ�ע��Ĳ���������֤
        boolean hasError = isInaccessibleViaGeneratedCode(clazz, "fields", element)
                || isInjectingInWrongPackage(clazz, element);

        if (hasError) {
            return;
        }
        TargetClass targetClass = getOrCreateTargetClass(targetClassMap, element, enclosingElement);

        String paramKey = "";
        if (clazz.equals(InjectParams.class)) {
            paramKey = element.getAnnotation(InjectParams.class).value();
        }
//        else if (clazz.equals(InjectUriParam.class)) {
//            paramKey = element.getAnnotation(InjectUriParam.class).value();
//        }
        String name = element.getSimpleName().toString();
        if (paramKey.length() == 0) {
            paramKey = name;
        }
        FiledInjecting filedInjecting = new FiledInjecting(name, element.asType(), paramKey, clazz);
        targetClass.addField(filedInjecting);
    }

    /**
     * �� targetClassMap �л�ȡ TargetClass, ����������򴴽�TargetClass
     */
    private TargetClass getOrCreateTargetClass(Map<TypeElement, TargetClass> targetClassMap, Element element, TypeElement enclosingElement) {

        TargetClass targetClass = targetClassMap.get(enclosingElement);

        if (targetClass == null) {
            TypeName targetType = TypeName.get(enclosingElement.asType());
            if (targetType instanceof ParameterizedTypeName) {
                targetType = ((ParameterizedTypeName) targetType).rawType;
            }

            String packageName = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, packageName);
            ClassName bindingClassName = ClassName.get(packageName, className + "_RouterInjecting");

            //boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);

            targetClass = new TargetClass(typeTools, targetType, bindingClassName);
            targetClassMap.put(enclosingElement, targetClass);
        }
        return targetClass;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private List<Class<InjectParams>> getSupportedAnnotations() {
        return ANNOTATIONS;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
                                                   String targetThing, Element element) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing type.
        if (enclosingElement.getKind() != CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing class visibility is not private.
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }
        return hasError;
    }

    private boolean isInjectingInWrongPackage(Class<? extends Annotation> annotationClass,
                                              Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        return false;
    }


    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(kind, message, element);
    }

}
