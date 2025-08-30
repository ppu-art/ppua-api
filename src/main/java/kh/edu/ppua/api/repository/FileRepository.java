package kh.edu.ppua.api.repository;

import kh.edu.ppua.api.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByStoredFileName(String storedFileName);

    List<FileEntity> findByEntityIdAndEntityType(Long entityId, String entityType);

    List<FileEntity> findByCategory(String category);

    @Query("SELECT f FROM FileEntity f WHERE f.entityType = :entityType AND f.entityId = :entityId AND f.category = :category")
    List<FileEntity> findByEntityAndCategory(@Param("entityType") String entityType,
                                             @Param("entityId") Long entityId,
                                             @Param("category") String category);

    @Query("SELECT f FROM FileEntity f WHERE f.fileType LIKE 'image/%'")
    List<FileEntity> findAllImages();

    void deleteByStoredFileName(String storedFileName);
}