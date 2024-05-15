
package com.example.accessingdataneo4j.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Patient {

    @Id
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private Family family;

    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.OUTGOING)
    private Study study;
}
