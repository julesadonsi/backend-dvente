package com.usetech.dvente.services.files;

import com.usetech.dvente.requests.files.ValidDocument;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class DocumentValidator implements ConstraintValidator<ValidDocument, MultipartFile> {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Le fichier est requis")
                    .addConstraintViolation();
            return false;
        }

        // Vérifier la taille du fichier
        if (file.getSize() > MAX_FILE_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Le fichier ne doit pas dépasser 10MB")
                    .addConstraintViolation();
            return false;
        }

        // Vérifier l'extension du fichier
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Le fichier doit être un PDF, DOC ou DOCX")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean hasValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
