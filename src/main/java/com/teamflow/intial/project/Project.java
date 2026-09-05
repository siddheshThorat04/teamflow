package com.teamflow.intial.project;

import com.teamflow.intial.common.BaseEntity;
import com.teamflow.intial.organization.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "projects",
    uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "key"})
)
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(name = "key", nullable = false, length = 10)
    private String key;

    private String description;
}