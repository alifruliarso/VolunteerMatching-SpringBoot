package com.galapea.techblog.volunteer_matching.organization;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Check that id is present and available when a new Organization is created.
 */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OrganizationIdValid.OrganizationIdValidValidator.class)
public @interface OrganizationIdValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OrganizationIdValidValidator implements ConstraintValidator<OrganizationIdValid, String> {

        private final OrganizationService organizationService;
        private final HttpServletRequest request;

        public OrganizationIdValidValidator(
                final OrganizationService organizationService, final HttpServletRequest request) {
            this.organizationService = organizationService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            @SuppressWarnings("unchecked")
            final Map<String, String> pathVariables =
                    ((Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null) {
                // only relevant for new objects
                return true;
            }
            String error = null;
            if (value == null) {
                // missing input
                error = "NotNull";
            } else if (organizationService.idExists(value)) {
                error = "Exists.organization.id";
            }
            if (error != null) {
                cvContext.disableDefaultConstraintViolation();
                cvContext
                        .buildConstraintViolationWithTemplate("{" + error + "}")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
    }
}
