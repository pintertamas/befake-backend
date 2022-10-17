package com.pintertamas.befake.interactionservice.repository;

import com.pintertamas.befake.interactionservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByPostId(Long postId);
}
