package com.pintertamas.befake.friendservice.service;

import com.pintertamas.befake.friendservice.exception.FriendshipException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.model.Status;
import com.pintertamas.befake.friendservice.model.User;
import com.pintertamas.befake.friendservice.proxy.UserProxy;
import com.pintertamas.befake.friendservice.repository.FriendshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserProxy userProxy;

    public FriendService(FriendshipRepository friendshipRepository, UserProxy userProxy) {
        this.friendshipRepository = friendshipRepository;
        this.userProxy = userProxy;
    }

    public Friendship sendFriendRequest(Long userId, Long friendId) throws FriendshipException, UserNotFoundException {
        Optional<Friendship> outgoingFriendship = friendshipRepository.findByUser1IdAndUser2Id(userId, friendId);
        Optional<Friendship> incomingFriendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        ResponseEntity<User> friend = userProxy.findUserByUserId(friendId);

        if (userId.equals(friendId)) throw new FriendshipException("You can't send request to yourself");
        if (friend.getBody() == null || !friend.getStatusCode().equals(HttpStatus.OK)) {
            throw new UsernameNotFoundException("User not found by: " + userId);
        }
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

    private Friendship getFriendshipByUserIdAndFriendId(Long userId, Long friendId) throws FriendshipException {
        Optional<Friendship> incomingFriendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        ResponseEntity<User> friend = userProxy.findUserByUserId(friendId);
        if (friend.getBody() == null || !friend.getStatusCode().equals(HttpStatus.OK)) {
            throw new UsernameNotFoundException("User not found by: " + userId);
        }
        if (incomingFriendship.isEmpty()) throw new FriendshipException("There is no relation between the two users");
        return incomingFriendship.get();
    }

    public Friendship acceptFriendRequest(Long userId, Long friendId) throws UserNotFoundException, FriendshipException {
        Friendship incomingFriendship = getFriendshipByUserIdAndFriendId(userId, friendId);
        incomingFriendship.setStatus(Status.ACCEPTED);
        incomingFriendship.setSince(new Timestamp(System.currentTimeMillis()));
        return friendshipRepository.save(incomingFriendship);
    }

    public void rejectFriendRequest(Long userId, Long friendId) {
        Optional<Friendship> friendship = friendshipRepository.findByUser1IdAndUser2Id(userId, friendId);
        friendship.ifPresent(friendshipRepository::delete);
        friendship = friendshipRepository.findByUser1IdAndUser2Id(friendId, userId);
        friendship.ifPresent(friendshipRepository::delete);
    }

    public List<Friendship> getFriendListByStatus(Long userId, Status status) {
        Optional<List<Friendship>> outgoingFriendship = friendshipRepository.findAllByUser1Id(userId);
        Optional<List<Friendship>> incomingFriendship = friendshipRepository.findAllByUser2Id(userId);
        List<Friendship> friendships = new ArrayList<>();

        addFilteredFriendshipsToList(friendships, outgoingFriendship, status);
        addFilteredFriendshipsToList(friendships, incomingFriendship, status);

        return friendships;
    }

    private void addFilteredFriendshipsToList(List<Friendship> friendships, Optional<List<Friendship>> list, Status status) {
        list.ifPresent(friendshipList -> friendships.addAll(friendshipList
                .stream()
                .filter((friendship) -> friendship.getStatus().equals(status))
                .toList()));
    }

    public List<Long> getListOfFriendIds(Long userId) {
        List<Long> friends = new ArrayList<>();
        getFriendListByStatus(userId, Status.ACCEPTED).forEach(friendship -> {
                    Long user1 = friendship.getUser1Id();
                    Long user2 = friendship.getUser2Id();
                    if (!user1.equals(userId)) friends.add(user1);
                    else friends.add(user2);
                }
        );
        return friends;
    }
}
