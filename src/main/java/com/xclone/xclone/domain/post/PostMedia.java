package com.xclone.xclone.domain.post;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer postId;
    private String fileName;
    private String mimeType;
    private String url;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public PostMedia() {}

    public PostMedia(Integer postId, String fileName, String mimeType, String url) {
        this.postId = postId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.url = url;
    }

    public Integer getId() { return id; }
    public Integer getPostId() { return postId; }
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public String getUrl() { return url; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}