package com.xclone.xclone.domain.feed;
import com.xclone.xclone.domain.post.Post;

public class PostRank {
    public Post post;
    public double affinity = 1.0;
    public double weight = 1.0;
    public double timeDecay = 0;
    public double totalScore = 0.0;

    public PostRank(Post post) {
        this.post = post;
    }

    public void computeTotalScore() {
        this.totalScore = affinity * weight * timeDecay;
    }
}