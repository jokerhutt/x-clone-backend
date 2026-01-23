package com.xclone.xclone.domain.like;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "liker_id")
    private Integer likerId;

    @Column(name = "post_id")
    private Integer likedPostId;

    public Integer getId() {
        return id;
    }

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLikerId() {
        return likerId;
    }

    public void setLikerId(Integer likerId) {
        this.likerId = likerId;
    }

    public Integer getLikedPostId() {
        return likedPostId;
    }

    public void setLikedPostId(Integer likedPostId) {
        this.likedPostId = likedPostId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

}
