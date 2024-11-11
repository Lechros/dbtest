package com.lechros.dbtest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String title;

    private String content;
}
