package kh.edu.ppua.api.service;

import kh.edu.ppua.api.dto.FileUploadReq;
import kh.edu.ppua.api.exception.ResourceNotFoundException;
import kh.edu.ppua.api.model.FileEntity;
import kh.edu.ppua.api.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.max-file-size:10485760}") // 10MB default
    private long maxFileSize;

    @Value("${file.allowed-extensions:jpg,jpeg,png,gif,pdf,doc,docx}")
    private String[] allowedExtensions;

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public FileUploadService(FileRepository fileRepository, FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }

    public FileEntity uploadFile(FileUploadReq uploadRequest) {
        MultipartFile file = uploadRequest.getFile();

        // Validate file
        fileStorageService.validateFile(file, maxFileSize, allowedExtensions);

        // Determine subdirectory based on entity type and category
        String subDirectory = getSubDirectory(uploadRequest.getEntityType(), uploadRequest.getCategory());

        // Store file on disk
        String storedFileName = fileStorageService.storeFile(file, subDirectory);

        // Create file entity
        FileEntity fileEntity = FileEntity.builder()
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(uploadDir + "/" + (subDirectory != null ? subDirectory + "/" : "") + storedFileName)
                .description(uploadRequest.getDescription())
                .category(uploadRequest.getCategory())
                .entityId(uploadRequest.getEntityId())
                .entityType(uploadRequest.getEntityType())
                .build();

        return fileRepository.save(fileEntity);
    }

    public FileEntity getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", id));
    }

    public FileEntity getFileByStoredName(String storedFileName) {
        return fileRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new ResourceNotFoundException("File", "storedFileName", storedFileName));
    }

    public List<FileEntity> getFilesByEntity(Long entityId, String entityType) {
        return fileRepository.findByEntityIdAndEntityType(entityId, entityType);
    }

    public List<FileEntity> getFilesByCategory(String category) {
        return fileRepository.findByCategory(category);
    }

    public List<FileEntity> getFilesByEntityAndCategory(Long entityId, String entityType, String category) {
        return fileRepository.findByEntityAndCategory(entityType, entityId, category);
    }

    public void deleteFile(Long id) {
        FileEntity fileEntity = getFile(id);

        // Delete file from storage
        String subDirectory = getSubDirectory(fileEntity.getEntityType(), fileEntity.getCategory());
        fileStorageService.deleteFile(fileEntity.getStoredFileName(), subDirectory);

        // Delete from database
        fileRepository.delete(fileEntity);
    }

    public void deleteFileByStoredName(String storedFileName) {
        FileEntity fileEntity = getFileByStoredName(storedFileName);
        deleteFile(fileEntity.getId());
    }

    public void deleteFilesByEntity(Long entityId, String entityType) {
        List<FileEntity> files = getFilesByEntity(entityId, entityType);
        files.forEach(file -> deleteFile(file.getId()));
    }

    public String getSubDirectory(String entityType, String category) {
        if (entityType != null) {
            String baseDir = entityType.toLowerCase() + "s"; // products, users, etc.
            if (category != null) {
                return baseDir + "/" + category.toLowerCase();
            }
            return baseDir;
        }
        return "general";
    }

    public boolean isImageFile(FileEntity fileEntity) {
        return fileEntity.getFileType() != null && fileEntity.getFileType().startsWith("image/");
    }
}