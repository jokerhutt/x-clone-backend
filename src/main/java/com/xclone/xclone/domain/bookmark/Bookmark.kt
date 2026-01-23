package com.xclone.xclone.domain.bookmark;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table (name = "bookmarks")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bookmarked_by")
    private Integer bookmarkedBy;

    @Column(name = "bookmarked_post")
    private Integer bookmarkedPost;

    @Column(name = "created_at", updatable = false, insertable = true)
    private Timestamp createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookmarkedBy() {
        return bookmarkedBy;
    }

    public void setBookmarkedBy(Integer bookmarkedBy) {
        this.bookmarkedBy = bookmarkedBy;
    }

    public Integer getBookmarkedPost() {
        return bookmarkedPost;
    }

    public void setBookmarkedPost(Integer bookmarkedPost) {
        this.bookmarkedPost = bookmarkedPost;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
