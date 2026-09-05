package com.teamflow.intial.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.organization.id = :orgId")
    List<Project> findByOrganizationId(@Param("orgId") Long orgId);

    Optional<Project> findByOrganizationIdAndKey(Long orgId, String key);

    boolean existsByOrganizationIdAndKey(Long orgId, String key);
}