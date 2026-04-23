package com.sachdeva.Aibot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name="comment")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long Id;
    private long postId;
    private long authorId;
    private String authorType;
    private int depth_level;
    private String content;
    private LocalDateTime createdAt;

    @PrePersist
    public void Prepersist(){
        createdAt=LocalDateTime.now();
    }

    @PreUpdate
    public void PreUpdate(){
        createdAt=LocalDateTime.now();
    }

}
