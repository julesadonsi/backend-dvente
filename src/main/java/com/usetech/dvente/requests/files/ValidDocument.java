package com.usetech.dvente.requests.files;


import com.usetech.dvente.services.files.DocumentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocumentValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDocument {
    String message() default "Le fichier doit être un PDF, DOC ou DOCX et ne pas dépasser 10MB";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
