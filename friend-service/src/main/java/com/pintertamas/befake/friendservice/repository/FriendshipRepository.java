package com.pintertamas.befake.friendservice.repository;

import com.pintertamas.befake.friendservice.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<List<Friendship>> findAllByUser1Id(Long userId);

    Optional<List<Friendship>> findAllByUser2Id(Long userId);

    Optional<Friendship> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
