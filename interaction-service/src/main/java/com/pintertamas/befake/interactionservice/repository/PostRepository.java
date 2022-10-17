package com.pintertamas.befake.interactionservice.repository;

import com.pintertamas.befake.interactionservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
