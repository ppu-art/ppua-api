package kh.edu.ppua.api.service;

import kh.edu.ppua.api.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        // Normalize file name
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }

        try {
            // Create subdirectory if it doesn't exist
            Path targetLocation = this.fileStorageLocation;
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                targetLocation = targetLocation.resolve(subDirectory);
                Files.createDirectories(targetLocation);
            }

            // Generate unique file name
            String fileExtension = getFileExtension(originalFileName);
            String storedFileName = generateUniqueFileName(originalFileName, fileExtension);

            // Copy file to the target location
            Path targetPath = targetLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return storedFileName;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, String subDirectory) {
        try {
            Path filePath = this.fileStorageLocation;
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                filePath = filePath.resolve(subDirectory);
            }
            filePath = filePath.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found " + fileName, ex);
        }
    }

    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = this.fileStorageLocation;
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                filePath = filePath.resolve(subDirectory);
            }
            filePath = filePath.resolve(fileName).normalize();

            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }

    public String getFileStoragePath(String subDirectory) {
        Path path = this.fileStorageLocation;
        if (subDirectory != null && !subDirectory.trim().isEmpty()) {
            path = path.resolve(subDirectory);
        }
        return path.toString();
    }

    private String generateUniqueFileName(String originalFileName, String fileExtension) {
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String uuid = java.util.UUID.randomUUID().toString();
        return uuid + "-" + baseName.replaceAll("[^a-zA-Z0-9]", "-") + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public void validateFile(MultipartFile file, long maxFileSize, String[] allowedExtensions) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file.");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileStorageException("File size exceeds the maximum allowed size.");
        }

        if (allowedExtensions != null && allowedExtensions.length > 0) {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName != null) {
                String fileExtension = getFileExtension(originalFileName).toLowerCase();
                boolean isValidExtension = false;
                for (String extension : allowedExtensions) {
                    if (("." + extension).equalsIgnoreCase(fileExtension)) {
                        isValidExtension = true;
                        break;
                    }
                }
                if (!isValidExtension) {
                    throw new FileStorageException("File type not allowed. Allowed types: " +
                            String.join(", ", allowedExtensions));
                }
            }
        }
    }
}