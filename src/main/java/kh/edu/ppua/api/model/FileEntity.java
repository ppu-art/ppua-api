package kh.edu.ppua.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "File entity representing uploaded files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the file", example = "1")
    private Long id;

    @Column(nullable = false, length = 255)
    @Schema(description = "Original file name", example = "product-image.jpg")
    private String originalFileName;

    @Column(nullable = false, length = 255)
    @Schema(description = "Stored file name (with UUID)", example = "a1b2c3d4-product-image.jpg")
    private String storedFileName;

    @Column(nullable = false, length = 50)
    @Schema(description = "File type", example = "image/jpeg")
    private String fileType;

    @Column(nullable = false)
    @Schema(description = "File size in bytes", example = "102400")
    private Long fileSize;

    @Column(nullable = false, length = 500)
    @Schema(description = "File storage path", example = "/uploads/images/products/a1b2c3d4-product-image.jpg")
    private String filePath;

    @Column(length = 500)
    @Schema(description = "File description", example = "Main product image")
    private String description;

    @Column(length = 50)
    @Schema(description = "File category", example = "PRODUCT_IMAGE")
    private String category;

    @Column(nullable = false)
    @Schema(description = "Upload timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime uploadedAt;

    @Column
    @Schema(description = "Associated entity ID", example = "1")
    private Long entityId;

    @Column(length = 50)
    @Schema(description = "Associated entity type", example = "PRODUCT")
    private String entityType;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}