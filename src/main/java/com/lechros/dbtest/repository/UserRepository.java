package com.lechros.dbtest.repository;

import com.lechros.dbtest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
