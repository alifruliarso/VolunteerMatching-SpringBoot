package com.galapea.techblog.volunteer_matching.organization_member;

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
 * Check that id is present and available when a new OrganizationMember is created.
 */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OrganizationMemberIdValid.OrganizationMemberIdValidValidator.class)
public @interface OrganizationMemberIdValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OrganizationMemberIdValidValidator implements ConstraintValidator<OrganizationMemberIdValid, String> {

        private final OrganizationMemberService organizationMemberService;
        private final HttpServletRequest request;

        public OrganizationMemberIdValidValidator(
                final OrganizationMemberService organizationMemberService, final HttpServletRequest request) {
            this.organizationMemberService = organizationMemberService;
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
            } else if (organizationMemberService.idExists(value)) {
                error = "Exists.organizationMember.id";
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
