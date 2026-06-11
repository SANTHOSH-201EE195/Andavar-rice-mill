package com.andavarmillklr.oil.masalas.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * ImageUploadService - Handles saving product images.
 *
 * For local development, this saves images to a "product-images" folder
 * in the project root. When deployed to production, this can be swapped
 * out for Cloudinary or AWS S3.
 */
@Service
public class ImageUploadService {

    // Directory where images will be saved locally
    private final String UPLOAD_DIR = "product-images/";

    public ImageUploadService() {
        // Create the directory if it doesn't exist when the service starts
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    /**
     * Save an uploaded file to the local filesystem.
     *
     * @param file The uploaded file
     * @return The URL path where the image can be accessed
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        // Generate a unique file name to prevent overwriting
        // e.g., "coconut.png" -> "123e4567-e89b-12d3-a456-426614174000_coconut.png"
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "image.png";
        }
        
        // Remove spaces and special characters from filename
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // Save the file
        Path destinationFile = Paths.get(UPLOAD_DIR).resolve(Paths.get(uniqueFilename))
                .normalize().toAbsolutePath();

        file.transferTo(destinationFile);

        // Return the URL path where the frontend can access it
        // application.properties maps "/product-images/**" to "file:./product-images/"
        return "/product-images/" + uniqueFilename;
    }

    /**
     * Delete an image from the local filesystem.
     *
     * @param imageUrl The URL path of the image (e.g., "/product-images/123_abc.png")
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/product-images/")) {
            return;
        }

        try {
            // Extract the filename from the URL
            String filename = imageUrl.substring("/product-images/".length());
            Path fileToDelete = Paths.get(UPLOAD_DIR).resolve(filename).normalize().toAbsolutePath();
            
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }
}
