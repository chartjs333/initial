
package com.example.accessingdataneo4j.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Family {

    @Id
    private String uuid;
    private String id;
    private String name;
    private Boolean history;
    private Integer numHetMutAffected;
    private Integer numHomMutAffected;
    private Integer numHetMutUnaffected;
    private Integer numHomMutUnaffected;
    private Integer numWildtypeAffected;
    private Integer numWildtypeUnaffected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Relationship(type = "HAS_PATIENT", direction = Relationship.Direction.OUTGOING)
    private Set<Patient> patients;

    @Relationship(type = "STUDY_FOR", direction = Relationship.Direction.INCOMING)
    private Study study;
}
