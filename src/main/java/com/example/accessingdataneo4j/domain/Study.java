package com.example.accessingdataneo4j.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Study {

    @Id
    private String id; // This can be the PMID or another unique identifier
    private String title;
    private String author;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private String diseaseAbbreviation;

//    @Relationship(type = "RELATED_TO", direction = Relationship.Direction.OUTGOING)
    @Relationship(type = "RELATED_FROM", direction = Relationship.Direction.INCOMING)
    private Set<Disease> diseases;
}
