package com.pintertamas.userservice.dto;

import com.pintertamas.userservice.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    String username;
    String password;
    String fullName;
    String email;
    String biography;
    String location;

    public User userFromDTO() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setBiography(biography);
        user.setLocation(location);
        return user;
    }

    public User userFromDTOWithId(Long id) {
        User user = userFromDTO();
        user.setId(id);
        return user;
    }
}
