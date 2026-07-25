package com.devboard.backend.repository;

import com.devboard.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByOwnerId(Long ownerId);

	Optional<Project> findByIdAndOwnerEmail(Long id, String email);

}
