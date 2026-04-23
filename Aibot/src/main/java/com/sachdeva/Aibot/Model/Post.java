package com.sachdeva.Aibot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name="post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private long authorId;
    private String content;
    private String authorType;
    private LocalDateTime createdAt;

    @PrePersist
    public void Prepersist(){
        createdAt=LocalDateTime.now();
    }
}
