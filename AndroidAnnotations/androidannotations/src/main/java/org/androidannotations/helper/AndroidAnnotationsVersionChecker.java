package org.androidannotations.helper;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.androidannotations.annotations.EActivity;

public class AndroidAnnotationsVersionChecker {

	private static final String PROCESSOR_JAR_VERSION = AndroidAnnotationsVersionChecker.class.getPackage().getImplementationVersion();

	private ProcessingEnvironment processingEnv;
	private TypeElement annotation;
	private RoundEnvironment roundEnv;

	public AndroidAnnotationsVersionChecker(//
			TypeElement annotation, //
			ProcessingEnvironment processingEnvironment, //
			RoundEnvironment roundEnv) {
		this.annotation = annotation;
		processingEnv = processingEnvironment;
		this.roundEnv = roundEnv;
	}

	public boolean isAPIJarInClassPath() {
		String apiJarVersion = null;
		String processJarVersion = PROCESSOR_JAR_VERSION;

		apiJarVersion = getVersion(EActivity.class);

		if (processJarVersion == null) {
			// Ok, we are not executing in a jar but into dev env (into eclipse)
			return true;
		}

		processJarVersion = " = " + processJarVersion;
		apiJarVersion = apiJarVersion != null ? " = " + apiJarVersion : " <= 2.6";

		if (processJarVersion.equals(apiJarVersion)) {
			return true;
		}

		String message = "It seems your are using different versions of AndroidAnnotations jars \nandroidannotations-api.jar " + apiJarVersion + " \nandroidannotations.jar " + processJarVersion + "\n";

		printError(annotation, roundEnv, message);

		return false;
	}

	private String getVersion(Class<?> clazz) {
		String apiJarVersion;
		URL res = clazz.getResource(clazz.getSimpleName() + ".class");
		JarURLConnection conn;
		try {

			URLConnection openConnection = res.openConnection();
			conn = (JarURLConnection) openConnection;
			Manifest mf = conn.getManifest();
			Attributes atts = mf.getMainAttributes();

			apiJarVersion = atts.getValue(Name.IMPLEMENTATION_VERSION);

		} catch (Exception e) {
			apiJarVersion = "UNKNOWN version of class " + EActivity.class.getName() + " because : " + e.getMessage();
		}
		return apiJarVersion;
	}

	private void printError(TypeElement annotation, RoundEnvironment roundEnv, String message) {
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.ERROR, message);

		Element element = roundEnv.getElementsAnnotatedWith(annotation).iterator().next();
		messager.printMessage(Diagnostic.Kind.ERROR, message, element);
	}

}
