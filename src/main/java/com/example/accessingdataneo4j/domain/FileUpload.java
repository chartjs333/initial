package com.example.accessingdataneo4j.domain;

import com.example.accessingdataneo4j.domain.Disease;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
public class FileUpload {

    @Id
    private String tsvFileFileName;
    private String tsvFileContentType;
    private Integer tsvFileFileSize;
    private LocalDateTime tsvFileUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Relationship(type = "UPLOADED_FOR", direction = Relationship.Direction.OUTGOING)
    private Set<Disease> diseases;
}

