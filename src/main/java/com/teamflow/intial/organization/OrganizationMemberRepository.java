package com.teamflow.intial.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {

    List<OrganizationMember> findByUserId(Long userId);

    Optional<OrganizationMember> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);

    @Query("SELECT om FROM OrganizationMember om " +
           "JOIN FETCH om.organization " +
           "WHERE om.user.id = :userId")
    List<OrganizationMember> findByUserIdWithOrganization(@Param("userId") Long userId);
}