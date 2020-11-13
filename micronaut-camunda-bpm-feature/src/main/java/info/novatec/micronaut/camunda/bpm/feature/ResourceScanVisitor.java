package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.FieldElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.scheduling.executor.$DefaultExecutorSelectorDefinitionClass;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ResourceScanVisitor implements TypeElementVisitor<ResourceScan, Object> {

    private static final String RESOURCES_DIR = "src/main/resources";

    private static ArrayList<String> resourceFiles = new ArrayList<>();

    @Override
    public void start(VisitorContext visitorContext) {
        System.err.println("Test4");
    }

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        //TODO: Versuchen @ResourceScan in Factory zu annotieren  --> Prio 1
        //TODO: Problem beim Übergeben von String[]

        String test = "testBlub";

        Optional<Path> projectDir = context.getProjectDir();

        resourceFiles = findResourceFiles(Paths.get(projectDir.get().toString(), RESOURCES_DIR).toFile(), new ArrayList<>());

        String[] resourceArray = new String[resourceFiles.size()];
        resourceArray = resourceFiles.toArray(resourceArray);


        element.annotate(ResourceScan.class, resourceScanAnnotationValueBuilder -> resourceScanAnnotationValueBuilder.member("test",new AnnotationClassValue<>(resourceFiles)));

    }

    private ArrayList<String> findResourceFiles(File folder, List<String> filePath) {

        //Verbessern

        String pattern = "(\\w)+\\.(bpmn|cmmn|dmn)";

        if (filePath == null) {
            filePath = new ArrayList<>();
        }

        if (folder.exists()) {
            File[] files = folder.listFiles();

           for( File file : files){
               if (file.getName().matches(pattern))
               resourceFiles.add(file.getName());
           }
        }
        return resourceFiles;
    }

}



