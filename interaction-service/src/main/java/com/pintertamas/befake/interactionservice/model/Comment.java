package com.pintertamas.befake.interactionservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Entity(name = "Comment")
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "username", updatable = false)
    private String username;

    @Column(name = "post_id", nullable = false, updatable = false)
    private Long postId;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "comment_time")
    @CreationTimestamp
    private Timestamp commentTime;
}
