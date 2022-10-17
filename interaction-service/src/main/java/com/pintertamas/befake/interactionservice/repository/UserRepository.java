package com.pintertamas.befake.interactionservice.repository;

import com.pintertamas.befake.interactionservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
