package com.convertic.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Base Entity class for entities which will hold creation and last modification date.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Version
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
}