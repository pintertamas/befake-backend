package com.pintertamas.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Entity(name = "User")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String fullName;

    @Column
    private String email;

    @Column
    private String biography;

    // just the name of their city or something
    @Column
    private String location;

    @Column
    private byte[] profilePicture;

    @Column
    @CreatedDate
    private Instant registrationDate;

    /*@JsonIgnore
    @OneToMany
    @JoinColumn(name = "user2_id", nullable = false)
    private List<User> friends;*/
}
