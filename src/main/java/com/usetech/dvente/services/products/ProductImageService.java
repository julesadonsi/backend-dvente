package com.usetech.dvente.services.products;

import com.usetech.dvente.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final FileStorageService fileStorageService;

    /**
     * Sauvegarde une image de produit et retourne le chemin
     * @param image le fichier image à sauvegarder
     * @return le chemin relatif de l'image sauvegardée
     * @throws IllegalArgumentException si l'image est invalide
     */
    public String saveProductImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier image fourni");
        }

        String contentType = image.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new IllegalArgumentException("Type de fichier non supporté. Formats acceptés: JPG, JPEG, PNG, GIF, WEBP");
        }

        long maxSizeInBytes = 5 * 1024 * 1024;
        if (image.getSize() > maxSizeInBytes) {
            throw new IllegalArgumentException("La taille de l'image ne doit pas dépasser 5MB");
        }

        return fileStorageService.saveProductImage(image);
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }
}