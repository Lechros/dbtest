package com.lechros.dbtest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class XMLEntity {

    @Id
    @GeneratedValue
    private Long IDId;
}
