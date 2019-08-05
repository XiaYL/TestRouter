package net.luculent.router;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 *
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private static final String argumentName = "moduleName";
    private static final String pkgName = "net.luculent.router";
    private static final String routeInit = "RouteInit";
    private static final String initMethod = "init";
    private static final String activityInject = "ActivityInject";
    private static final String activityInjectName = "activityInject";
    private static final String fieldInject = "FieldInject";
    private static final String fieldInjectName = "fieldInject";
    private static final String menuInject = "MenuInject";
    private static final String menuInjectName = "menuInject";

    private static final ClassName ROUTER = ClassName.get(pkgName, "Router");
    private static final ClassName FIELD_INJECTOR = ClassName.get(pkgName, "FieldInjector");
    private static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    private static final ClassName LIST = ClassName.get("java.util", "List");
    private static final ClassName FIELD_MAPPING = ClassName.get(pkgName, "FieldMapping");
    private static final ClassName MENU_MAPPING = ClassName.get(pkgName, "MenuMapping");
    private static final ClassName UTILS = ClassName.get(pkgName, "Utils");

    private Filer filer;
    private Elements elements;
    private String moduleName = "";//moduleName
    private File manifestDir;//file dir where androidManifest.xml is in
    private boolean isLib;//mark lib project

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
        elements = env.getElementUtils();
        Map<String, String> options = env.getOptions();
        for (String key : options.keySet()) {
            if (argumentName.equals(key)) {
                moduleName = options.get(key);
                break;
            }
        }
        checkManifest();
    }

    private void checkManifest() {
        try {
            manifestDir = AndroidManifestHelper.getManifestDir(filer);
            isLib = !new File(manifestDir, "full").exists();//unchecked, a trick to check if project is library
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Override.class.getCanonicalName());//ensure file can be created
        types.add(Modules.class.getCanonicalName());
        types.add(Route.class.getCanonicalName());
        types.add(ParamInject.class.getCanonicalName());
        //add menu config
        types.add(EntryMenu.class.getCanonicalName());
        //2018-08-17 auto register activity in manifest
        types.add(MActivity.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        generateInit(roundEnv);

        Set<Element> elements = (Set<Element>) roundEnv.getElementsAnnotatedWith(Route.class);
        //route
        parseRouteBindings(elements);
        //param inject
        elements = (Set<Element>) roundEnv.getElementsAnnotatedWith(ParamInject.class);
        parseFieldBindings(elements);
        //bind value to field
        bindFieldValues(elements);
        //bind menu
        elements = (Set<Element>) roundEnv.getElementsAnnotatedWith(EntryMenu.class);
        parseMenuBindings(elements);

        elements = (Set<Element>) roundEnv.getElementsAnnotatedWith(MActivity.class);
        AndroidManifestHelper.createAptManifest(manifestDir, elements);
        return true;
    }

    private void generateInit(RoundEnvironment env) {
        if (isLib) {//lib project,no need init
            return;
        }
        Set<Element> moduleEles = (Set<Element>) env.getElementsAnnotatedWith(Modules.class);//only register in app
        // module once
        MethodSpec.Builder init = MethodSpec.methodBuilder(initMethod);
        init.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        if (!moduleEles.isEmpty()) {//has modules,if true,means both app and other modules has specified moduleName
            // by apt-arguments
            Modules modules = moduleEles.iterator().next().getAnnotation(Modules.class);
            String[] values = modules.value();
            for (String module : values) {
                initInject(init, module);
            }
        } else {//only app, no other modules and no moduleName specified
            initInject(init, moduleName);
        }
        TypeSpec routeInject = TypeSpec.classBuilder(routeInit)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(init.build())
                .build();
        try {
            JavaFile javaFile = JavaFile.builder(pkgName, routeInject)
                    .build();
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initInject(MethodSpec.Builder builder, String module) {
        String connector = ".";
        if (module.length() != 0) {
            connector = "_".concat(module).concat(".");
        }
        builder.addStatement(activityInject + connector + activityInjectName + "()");
        builder.addStatement(fieldInject + connector + fieldInjectName + "()");
        builder.addStatement(menuInject + connector + menuInjectName + "()");
    }

    private void parseRouteBindings(Set<Element> elements) {
        MethodSpec.Builder inject = MethodSpec.methodBuilder(activityInjectName);
        inject.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        for (Element element : elements) {
            Route router = element.getAnnotation(Route.class);
            for (String route : router.value()) {
                ClassName className = ClassName.get((TypeElement) element);
                inject.addStatement("$T.activityInject($S, $T.class)", ROUTER, route, className);
                inject.addCode("\n");
            }
        }
        String fileName = activityInject;
        if (moduleName.length() != 0) {
            fileName = activityInject + "_" + moduleName;
        }
        TypeSpec routeInject = TypeSpec.classBuilder(fileName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(inject.build())
                .build();
        try {
            JavaFile javaFile = JavaFile.builder(pkgName, routeInject)
                    .build();
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFieldBindings(Set<Element> elements) {
        MethodSpec.Builder inject = MethodSpec.methodBuilder(fieldInjectName);
        inject.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        for (Element element : elements) {
            //
            String field = element.getSimpleName().toString();
            //
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            ClassName className = ClassName.get(enclosingElement);
            ParamInject paramInject = element.getAnnotation(ParamInject.class);
            inject.addStatement("$T.fieldInject($T.class, new $T($S,$S,$S,$S))", ROUTER, className, FIELD_MAPPING,
                    field, paramInject.value(), paramInject.type().name(), paramInject.init());
        }
        String fileName = fieldInject;
        if (moduleName.length() != 0) {
            fileName = fieldInject + "_" + moduleName;
        }
        TypeSpec filedInject = TypeSpec.classBuilder(fileName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(inject.build())
                .build();
        try {
            JavaFile javaFile = JavaFile.builder(pkgName, filedInject)
                    .build();
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindFieldValues(Set<Element> elementSet) {
        Map<TypeElement, Set<Element>> map = new LinkedHashMap<>();
        for (Element element : elementSet) {
            //
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            Set<Element> set = map.get(enclosingElement);
            if (set == null) {
                set = new HashSet<>();
            }
            set.add(element);
            map.put(enclosingElement, set);
        }
        for (Map.Entry<TypeElement, Set<Element>> entry : map.entrySet()) {
            MethodSpec.Builder inject = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeVariableName.get("T"), "target")
                    .addParameter(ParameterizedTypeName.get(LIST, FIELD_MAPPING), "mappings")
                    //.addStatement("this.target = target")
                    .addStatement("$T bundle = target.getIntent().getExtras()", BUNDLE);
            MethodSpec.Builder reset = MethodSpec.methodBuilder("reset").addModifiers(Modifier.PUBLIC).addAnnotation
                    (Override.class)
//                    .addStatement("if (target == null) throw new $T($S)", IllegalStateException.class, "Bindings " +
//                            "already cleared")
                    ;
            for (Element element : entry.getValue()) {
                //
                String field = element.getSimpleName().toString();
                TypeName fieldType = TypeName.get(element.asType());
                if (fieldType instanceof ParameterizedTypeName) {
                    fieldType = ((ParameterizedTypeName) fieldType).rawType;
                }
                inject.addStatement("target.$L = $T.castValue(bundle, $S, mappings, $T.class)", field, UTILS, field,
                        fieldType);
                //reset.addStatement("target.$L = null", field);
            }
            //reset.addStatement("target = null");
            TypeName targetType = TypeName.get(entry.getKey().asType());
            if (targetType instanceof ParameterizedTypeName) {
                targetType = ((ParameterizedTypeName) targetType).rawType;
            }
            TypeSpec filedInject = TypeSpec.classBuilder(entry.getKey().getSimpleName().toString() + "_ParamInject")
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(TypeVariableName.get("T", targetType))
                    .addSuperinterface(FIELD_INJECTOR)
                    //.addField(TypeVariableName.get("T"), "target")
                    .addMethod(inject.build())
                    .addMethod(reset.build())
                    .build();
            try {
                PackageElement packageElement = elements.getPackageOf(entry.getKey());
                JavaFile javaFile = JavaFile.builder(packageElement.toString(), filedInject)
                        .build();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseMenuBindings(Set<Element> elements) {
        MethodSpec.Builder inject = MethodSpec.methodBuilder(menuInjectName);
        inject.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        for (Element element : elements) {
            EntryMenu entryMenu = element.getAnnotation(EntryMenu.class);
            ClassName className = ClassName.get((TypeElement) element);
            for (Menu menu : entryMenu.value()) {
                inject.addStatement("$T.menuInject($S, new $T($T.class,$S,$S,$S))", ROUTER, menu.nodeId(),
                        MENU_MAPPING, className, menu.menu(), menu.nodeId(), menu.icon());
            }
        }
        String fileName = menuInject;
        if (moduleName.length() != 0) {
            fileName = menuInject + "_" + moduleName;
        }
        TypeSpec routeInject = TypeSpec.classBuilder(fileName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(inject.build())
                .build();
        try {
            JavaFile javaFile = JavaFile.builder(pkgName, routeInject)
                    .build();
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
