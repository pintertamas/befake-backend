package com.pintertamas.befake.friendservice.repository;

import com.pintertamas.befake.friendservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
