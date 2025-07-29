package com.example.taskmanagement.repositories;

import com.example.taskmanagement.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    // Existing methods
    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE p.ownerId = :ownerId")
    List<Task> findAllByOwnerId(String ownerId);

    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE t.project.id = :projectId AND p.ownerId = :ownerId")
    List<Task> findByProjectIdAndOwnerId(UUID projectId, String ownerId);

    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE t.id = :taskId AND p.ownerId = :ownerId")
    Optional<Task> findByIdAndOwnerId(UUID taskId, String ownerId);
    
    // Paginated methods
    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE p.ownerId = :ownerId")
    Page<Task> findAllByOwnerIdPaginated(String ownerId, Pageable pageable);

    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE t.project.id = :projectId AND p.ownerId = :ownerId")
    Page<Task> findByProjectIdAndOwnerIdPaginated(UUID projectId, String ownerId, Pageable pageable);
}
