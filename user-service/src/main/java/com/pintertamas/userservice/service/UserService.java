package com.pintertamas.userservice.service;

import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService {
    final UserRepository userRepository;

    private final PasswordEncoder bcryptEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder bcryptEncoder) {
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    public User register(User newUser) throws UserExistsException {
        if (userRepository.findUserByUsername(newUser.getUsername()) != null || userRepository.findUserByEmail(newUser.getEmail()) != null) {
            throw new UserExistsException(newUser);
        }
        newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));

        newUser.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(newUser);
    }

    public User getUserData(Long userId) throws UserNotFoundException {
        try {
            return userRepository.findUserById(userId);
        } catch (Exception e) {
            throw new UserNotFoundException(userId);
        }
    }

    public List<User> getListOfUsers() throws UserNotFoundException {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public void updateProfile(User editedUser) throws UserNotFoundException {
        User user = userRepository.findUserByEmail(editedUser.getEmail());
        if (user == null) {
            throw new UserNotFoundException(editedUser);
        }

        editedUser.setId(user.getId());
        userRepository.save(editedUser);
    }

    public void updateProfilePicture() {
        //todo
    }

    public void delete(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        userRepository.delete(user);
    }
}
