package kh.edu.ppua.api.comtroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kh.edu.ppua.api.dto.FileUploadReq;
import kh.edu.ppua.api.dto.FileUploadRes;
import kh.edu.ppua.api.model.FileEntity;
import kh.edu.ppua.api.service.FileStorageService;
import kh.edu.ppua.api.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Controller", description = "APIs for file upload and download")
public class FileController {

    private final FileUploadService fileService;
    private final FileStorageService fileStorageService;

    @Autowired
    public FileController(FileUploadService fileService, FileStorageService fileStorageService) {
        this.fileService = fileService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file", description = "Upload a file with metadata")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or data")
    })
    public ResponseEntity<FileUploadRes> uploadFile(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "File description")
            @RequestParam(required = false) String description,
            @Parameter(description = "File category")
            @RequestParam(required = false) String category,
            @Parameter(description = "Associated entity ID")
            @RequestParam(required = false) Long entityId,
            @Parameter(description = "Associated entity type")
            @RequestParam(required = false) String entityType) {

        FileUploadReq uploadRequest = new FileUploadReq();
        uploadRequest.setFile(file);
        uploadRequest.setDescription(description);
        uploadRequest.setCategory(category);
        uploadRequest.setEntityId(entityId);
        uploadRequest.setEntityType(entityType);

        FileEntity fileEntity = fileService.uploadFile(uploadRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileUploadRes(fileEntity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file metadata", description = "Get file metadata by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File metadata found"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<FileUploadRes> getFileMetadata(
            @Parameter(description = "File ID", required = true, example = "1")
            @PathVariable Long id) {

        FileEntity fileEntity = fileService.getFile(id);
        return ResponseEntity.ok(new FileUploadRes(fileEntity));
    }

    @GetMapping("/download/{storedFileName:.+}")
    @Operation(summary = "Download a file", description = "Download a file by its stored name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Stored file name", required = true, example = "a1b2c3d4-product-image.jpg")
            @PathVariable String storedFileName,
            HttpServletRequest request) {

        FileEntity fileEntity = fileService.getFileByStoredName(storedFileName);
        String subDirectory = fileService.getSubDirectory(fileEntity.getEntityType(), fileEntity.getCategory());

        Resource resource = fileStorageService.loadFileAsResource(storedFileName, subDirectory);

        // Determine content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to default content type
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileEntity.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/view/{storedFileName:.+}")
    @Operation(summary = "View a file", description = "View a file (inline) by its stored name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File displayed successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Resource> viewFile(
            @Parameter(description = "Stored file name", required = true, example = "a1b2c3d4-product-image.jpg")
            @PathVariable String storedFileName,
            HttpServletRequest request) {

        FileEntity fileEntity = fileService.getFileByStoredName(storedFileName);

        if (!fileService.isImageFile(fileEntity)) {
            return ResponseEntity.badRequest().body(null);
        }

        String subDirectory = fileService.getSubDirectory(fileEntity.getEntityType(), fileEntity.getCategory());
        Resource resource = fileStorageService.loadFileAsResource(storedFileName, subDirectory);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or determine dynamically
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileEntity.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get files by entity", description = "Get all files for a specific entity")
    @ApiResponse(responseCode = "200", description = "Files retrieved successfully")
    public ResponseEntity<List<FileUploadRes>> getFilesByEntity(
            @Parameter(description = "Entity type", required = true, example = "PRODUCT")
            @PathVariable String entityType,
            @Parameter(description = "Entity ID", required = true, example = "1")
            @PathVariable Long entityId) {

        List<FileEntity> files = fileService.getFilesByEntity(entityId, entityType);
        List<FileUploadRes> responses = files.stream()
                .map(FileUploadRes::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a file", description = "Delete a file by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "File ID", required = true, example = "1")
            @PathVariable Long id) {

        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-multiple")
    @Operation(summary = "Upload multiple files", description = "Upload multiple files at once")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Files uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid files or data")
    })
    public ResponseEntity<List<FileUploadRes>> uploadMultipleFiles(
            @Parameter(description = "Files to upload", required = true)
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "File description")
            @RequestParam(required = false) String description,
            @Parameter(description = "File category")
            @RequestParam(required = false) String category,
            @Parameter(description = "Associated entity ID")
            @RequestParam(required = false) Long entityId,
            @Parameter(description = "Associated entity type")
            @RequestParam(required = false) String entityType) {

        List<FileUploadRes> responses = Arrays.stream(files)
                .map(file -> {
                    FileUploadReq uploadRequest = new FileUploadReq();
                    uploadRequest.setFile(file);
                    uploadRequest.setDescription(description);
                    uploadRequest.setCategory(category);
                    uploadRequest.setEntityId(entityId);
                    uploadRequest.setEntityType(entityType);

                    FileEntity fileEntity = fileService.uploadFile(uploadRequest);
                    return new FileUploadRes(fileEntity);
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}