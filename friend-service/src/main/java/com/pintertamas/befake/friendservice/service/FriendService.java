package com.pintertamas.befake.friendservice.service;

import com.pintertamas.befake.friendservice.exception.FriendshipException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.model.Status;
import com.pintertamas.befake.friendservice.model.User;
import com.pintertamas.befake.friendservice.repository.FriendshipRepository;
import com.pintertamas.befake.friendservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public Friendship sendFriendRequest(Long userId, Long friendId) throws FriendshipException, UserNotFoundException {
        Optional<Friendship> outgoingFriendship = friendshipRepository.findByUser1IdAndUser2Id(userId, friendId);
        Optional<Friendship> incomingFriendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        Optional<User> friend = userRepository.findById(friendId);

        if (userId.equals(friendId)) throw new FriendshipException("You can't send request to yourself");
        if (friend.isEmpty()) throw new UserNotFoundException(friendId.toString());
        if (outgoingFriendship.isPresent())
            throw new FriendshipException("Request already sent");
        if (incomingFriendship.isPresent())
            throw new FriendshipException("This is already a pending request, accept it");

        Friendship friendship = new Friendship();
        friendship.setUser1Id(userId);
        friendship.setUser2Id(friendId);
        friendship.setStatus(Status.PENDING);
        friendship.setSince(new Timestamp(System.currentTimeMillis()));
        return friendshipRepository.save(friendship);
    }

    public Friendship acceptFriendRequest(Long userId, Long friendId) throws UserNotFoundException, FriendshipException {
        Optional<Friendship> incomingFriendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        Optional<User> friend = userRepository.findById(friendId);

        if (friend.isEmpty()) throw new UserNotFoundException(friendId.toString());
        if (incomingFriendship.isEmpty()) throw new FriendshipException("There is no invitation to accept");

        incomingFriendship.get().setStatus(Status.ACCEPTED);
        incomingFriendship.get().setSince(new Timestamp(System.currentTimeMillis()));
        return friendshipRepository.save(incomingFriendship.get());
    }

    public void rejectFriendRequest(Long userId, Long friendId) throws UserNotFoundException, FriendshipException {
        Optional<Friendship> incomingFriendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        Optional<User> friend = userRepository.findById(friendId);

        if (friend.isEmpty()) throw new UserNotFoundException(friendId.toString());
        if (incomingFriendship.isEmpty()) throw new FriendshipException("There is no invitation to accept");

        friendshipRepository.delete(incomingFriendship.get());
    }
}
