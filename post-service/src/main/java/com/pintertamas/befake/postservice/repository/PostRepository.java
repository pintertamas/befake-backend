package com.pintertamas.befake.postservice.repository;

import com.pintertamas.befake.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<List<Post>> findAllByUserId(Long userId);

    Optional<List<Post>> findAllByUserIdAndPostingTimeAfter(Long userId, Timestamp pastTimestamp);
}
