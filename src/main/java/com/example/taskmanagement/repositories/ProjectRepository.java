package com.example.taskmanagement.repositories;

import com.example.taskmanagement.model.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.ownerId = :ownerId")
    List<Project> findAllWithTasks(String ownerId);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.ownerId = :ownerId")
    Page<Project> findAllWithTasksPaginated(String ownerId, Pageable pageable);
    
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id AND p.ownerId = :ownerId")
    Optional<Project> findByIdAndOwnerId(String ownerId, UUID id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Project p WHERE p.id = :id AND p.ownerId = :ownerId")
    void deleteByIdAndOwnerId(String ownerId, UUID id);
}
