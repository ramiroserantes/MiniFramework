package com.daarthy.mini.constructors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes(value = "com.daarthy.mini.constructors.MiniData")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class MiniDataProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(MiniData.class)) {
            System.out.println("Found an element annotated with @MiniData: " + element.getSimpleName());
        }
        return true;
    }
}