package kh.edu.ppua.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "File upload request")
public class FileUploadReq {

    @Schema(description = "File to upload", required = true)
    private MultipartFile file;

    @Schema(description = "File description", example = "Main product image")
    private String description;

    @Schema(description = "File category", example = "PRODUCT_IMAGE")
    private String category;

    @Schema(description = "Associated entity ID", example = "1")
    private Long entityId;

    @Schema(description = "Associated entity type", example = "PRODUCT")
    private String entityType;
}