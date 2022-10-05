package com.pintertamas.befake.authorizationservice.repository;

import com.pintertamas.befake.authorizationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
