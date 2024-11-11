package com.lechros.dbtest.repository;

import com.lechros.dbtest.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
