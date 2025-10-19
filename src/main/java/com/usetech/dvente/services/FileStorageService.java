package com.usetech.dvente.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.path:uploads}")
    private String uploadBasePath;


    @Value("${server.backend.url}")
    private String apiUrl;

    /**
     * Sauvegarde une image de produit et retourne son URL.
     */
    public String saveProductImage(MultipartFile file) {
        return saveFile(file, "products");
    }

    /**
     * Supprime une image de produit.
     */
    public void deleteProductImage(String imageUrl) {
        deleteFile(imageUrl);
    }

    /**
     * Sauvegarde un fichier avatar et retourne son URL.
     */
    public String saveAvatar(MultipartFile file) {
        return saveFile(file, "avatars");
    }

    /**
     * Supprime un fichier avatar.
     */
    public void deleteAvatar(String avatarUrl) {
        deleteFile(avatarUrl);
    }

    /**
     * Sauvegarde un document (PDF, DOC, DOCX) et retourne son URL.
     *
     * @param file le fichier à sauvegarder
     * @param documentType le type de document (ex: "ifu", "rcm")
     * @return l'URL du fichier sauvegardé
     */
    public String saveDocument(MultipartFile file, String documentType) {
        return saveFile(file, "documents/" + documentType);
    }

    /**
     * Supprime un document.
     */
    public void deleteDocument(String documentUrl) {
        deleteFile(documentUrl);
    }

    /**
     * Sauvegarde un fichier dans un sous-dossier spécifique.
     */
    private String saveFile(MultipartFile file, String subFolder) {
        try {
            String fileDir = uploadBasePath + "/" + subFolder;
            Path uploadPath = Paths.get(fileDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subFolder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Échec de la sauvegarde du fichier: " + e.getMessage());
        }
    }

    /**
     * Supprime un fichier du disque.
     */
    private void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String relativePath = fileUrl.startsWith("/uploads/")
                    ? fileUrl.substring("/uploads/".length())
                    : fileUrl;

            Path filePath = Paths.get(uploadBasePath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Fichier supprimé : " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
    }

    /**
     * Vérifie si un fichier existe.
     */
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        String relativePath = fileUrl.startsWith("/uploads/")
                ? fileUrl.substring("/uploads/".length())
                : fileUrl;

        Path filePath = Paths.get(uploadBasePath, relativePath);
        return Files.exists(filePath);
    }


    public String saveAvatarFromUrl(String imageUrl, UUID userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);

            if (imageBytes == null || imageBytes.length == 0) {
                return null;
            }

            String fileDir = uploadBasePath + "/avatars";
            Path uploadPath = Paths.get(fileDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = "user_" + userId + "_" + UUID.randomUUID().toString() + ".jpg";
            Path filePath = uploadPath.resolve(filename);

            Files.write(filePath, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return "/uploads/avatars/" + filename;

        } catch (Exception e) {
            System.err.println("Erreur lors du téléchargement de l'avatar: " + e.getMessage());
            return null;
        }
    }
}