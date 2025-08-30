package kh.edu.ppua.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kh.edu.ppua.api.model.FileEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "File response")
public class FileUploadRes {

    @Schema(description = "File ID", example = "1")
    private Long id;

    @Schema(description = "Original file name", example = "product-image.jpg")
    private String originalFileName;

    @Schema(description = "Stored file name", example = "a1b2c3d4-product-image.jpg")
    private String storedFileName;

    @Schema(description = "File type", example = "image/jpeg")
    private String fileType;

    @Schema(description = "File size in bytes", example = "102400")
    private Long fileSize;

    @Schema(description = "File URL", example = "/api/files/download/a1b2c3d4-product-image.jpg")
    private String fileUrl;

    @Schema(description = "File description", example = "Main product image")
    private String description;

    @Schema(description = "File category", example = "PRODUCT_IMAGE")
    private String category;

    @Schema(description = "Upload timestamp")
    private LocalDateTime uploadedAt;

    @Schema(description = "Associated entity ID", example = "1")
    private Long entityId;

    @Schema(description = "Associated entity type", example = "PRODUCT")
    private String entityType;

    public FileUploadRes(FileEntity fileEntity) {
        this.id = fileEntity.getId();
        this.originalFileName = fileEntity.getOriginalFileName();
        this.storedFileName = fileEntity.getStoredFileName();
        this.fileType = fileEntity.getFileType();
        this.fileSize = fileEntity.getFileSize();
        this.fileUrl = "/api/files/download/" + fileEntity.getStoredFileName();
        this.description = fileEntity.getDescription();
        this.category = fileEntity.getCategory();
        this.uploadedAt = fileEntity.getUploadedAt();
        this.entityId = fileEntity.getEntityId();
        this.entityType = fileEntity.getEntityType();
    }
}