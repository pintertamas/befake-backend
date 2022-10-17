package com.pintertamas.befake.interactionservice.repository;

import com.pintertamas.befake.interactionservice.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<List<Reaction>> findAllByPostId(Long postId);
    Optional<Reaction> findByPostIdAndUserId(Long postId, Long userId);
}
