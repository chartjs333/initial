package com.example.accessingdataneo4j.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Node
public class Disease {

    @Id
    private String abbreviation;

    private String name;

    private Boolean launched;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Relationship(type = "PARENT")
    private Set<Disease> parentDiseases;

    public Disease(String name, String abbreviation, Boolean launched, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.launched = launched;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
}

    public void addParent(Disease disease) {
        if (parentDiseases == null) {
            parentDiseases = new HashSet<>();
        }
        parentDiseases.add(disease);
    }

    public String toString() {
        return this.name + "'s Parent Diseases => " +
                Optional.ofNullable(this.parentDiseases).orElse(Collections.emptySet()).stream()
                        .map(Disease::getName)
                        .collect(Collectors.toList());
    }
}