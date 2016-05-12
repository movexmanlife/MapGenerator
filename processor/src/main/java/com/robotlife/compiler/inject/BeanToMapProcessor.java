package com.robotlife.compiler.inject;

import com.robotlife.compiler.annotation.Maps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * robotlife
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BeanToMapProcessor extends AbstractProcessor {
    public static final String POSTFIX = "$$BeanMap";

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        filer = env.getFiler();
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<Element, ClassGenerator> targetClassMap = findAndParseTargets(roundEnv);
        writeLog("process()------------------------------------");

        for (Map.Entry<Element, ClassGenerator> entry : targetClassMap.entrySet()) {
            Element typeElement = entry.getKey();
            ClassGenerator injector = entry.getValue();
            try {
                String value = injector.brewJava();

                JavaFileObject jfo = filer.createSourceFile(injector.getFqcn(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(value);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                if (processingEnv != null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
                }
            }
        }


        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Maps.class.getCanonicalName());
        return supportTypes;
    }

    private Map<Element, ClassGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<Element, ClassGenerator> targetClassMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(Maps.class)) {
            ClassGenerator injector = generatorClass(targetClassMap, element);
        }
        return targetClassMap;
    }

    /**
     * @param targetClassMap
     * @param element
     * @return
     */
    private ClassGenerator generatorClass(Map<Element, ClassGenerator> targetClassMap, Element element) {
        ClassGenerator injector = targetClassMap.get(element);
        TypeElement enclosingElement = (TypeElement)(element);
        if (injector == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            // original Class package
            String classPackage = getPackageName(enclosingElement);
            // original Class name
            String className = getClassName(enclosingElement, classPackage) + POSTFIX;

            List<String> list = new ArrayList<>();
            for (Element enclosedElement : enclosingElement.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    list.add(enclosedElement.getSimpleName().toString());
                }
            }

            checkValidate(enclosingElement);

            injector = new ClassGenerator(classPackage, className, targetType, list.toArray(new String[list.size()]));
            targetClassMap.put(enclosingElement, injector);
        }
        return injector;
    }

    private void checkValidate(Element element) {
        TypeMirror elementType = element.asType();

        if (isInterface(elementType)) {
            throw new IllegalArgumentException("Not support 'interface' to 'Map', you can use 'Class' instead");
        }
    }

    private boolean isInterface(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }
        return ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.INTERFACE;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     *
     * @param type
     * @param packageName
     * @return
     */
    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    /**
     *
     * @param type
     * @return
     */
    private String getPackageName(TypeElement  type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void writeLog(String str) {
        try {
            FileWriter fw = new FileWriter(new File("D://process.txt"), true);
            fw.write(str + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
