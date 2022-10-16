package com.pintertamas.befake.postservice.repository;

import com.pintertamas.befake.postservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
