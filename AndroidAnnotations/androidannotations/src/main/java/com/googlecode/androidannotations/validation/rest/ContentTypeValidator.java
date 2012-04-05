package com.googlecode.androidannotations.validation.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.rest.ContentType;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.ElementValidator;
import com.googlecode.androidannotations.validation.IsValid;

public class ContentTypeValidator implements ElementValidator {

	private ValidatorHelper validatorHelper;

	public ContentTypeValidator(ProcessingEnvironment processingEnv) {
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ContentType.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		// validatorHelper.notAlreadyValidated(element, validatedElements,
		// valid);

		// Interface annotated
		if (element instanceof TypeElement) {

			validatorHelper.elementHasRestAnnotation(element, validatedElements, valid);

			// Method Annotated
		} else {
			ExecutableElement executableElement = (ExecutableElement) element;

			validatorHelper.enclosingElementHasRestAnnotation(executableElement, validatedElements, valid);

			validatorHelper.elementHasGetOrPostAnnotation(executableElement, validatedElements, valid);

			validatorHelper.throwsOnlyRestClientException(executableElement, valid);

		}

		// TODO Check if has JSON Parser API (Jackson, GSon ...)

		return valid.isValid();
	}

}
