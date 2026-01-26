USE xclone_db;

create table if not exists xclone_db.bookmarks
(
    id              int not null auto_increment primary key,
    bookmarked_post int                                 not null,
    bookmarked_by   int                                 not null,
    created_at      timestamp default CURRENT_TIMESTAMP not null
);

create index bookmarked_by
    on xclone_db.bookmarks (bookmarked_by);

create index bookmarked_post
    on xclone_db.bookmarks (bookmarked_post);

create table if not exists xclone_db.feed_entry
(
    id       bigint unsigned not null auto_increment primary key,
    user_id  int    not null,
    post_id  int    not null,
    score    double not null,
    position int    not null
);

create index post_id
    on xclone_db.feed_entry (post_id);

create index user_id
    on xclone_db.feed_entry (user_id);


create table if not exists xclone_db.feedback
(
    id         int not null auto_increment primary key,
    user_id    int                                 null,
    text       text                                not null,
    type       varchar(50)                         not null,
    created_at timestamp default CURRENT_TIMESTAMP null
);

create table if not exists xclone_db.follows_seq
(
    next_val bigint null
);

create table if not exists xclone_db.likes
(
    id         int not null auto_increment primary key,
    liker_id   int                                 not null,
    post_id    int                                 not null,
    created_at timestamp default CURRENT_TIMESTAMP not null
);

create index liker_id
    on xclone_db.likes (liker_id);

create index post_id
    on xclone_db.likes (post_id);

create table if not exists xclone_db.notifications
(
    id         int not null auto_increment primary key,
    receiver_id  int                                  not null,
    sender_id    int                                  not null,
    type         varchar(255)                         not null,
    reference_id int                                  null,
    text         text                                 null,
    seen         tinyint(1) default 0                 not null,
    created_at   timestamp  default CURRENT_TIMESTAMP null
);

create index fk_notification_receiver
    on xclone_db.notifications (receiver_id);

create index fk_notification_sender
    on xclone_db.notifications (sender_id);

create table if not exists xclone_db.poll_choices
(
    id         int not null auto_increment primary key,
    poll_id    int           not null,
    choice     text          not null,
    vote_count int default 0 null
);

create table if not exists xclone_db.poll_votes
(
    id             int not null auto_increment primary key,
    poll_id        int                                 not null,
    poll_choice_id int                                 not null,
    user_id        int                                 not null,
    created_at     timestamp default CURRENT_TIMESTAMP not null
);

create unique index unique_vote
    on xclone_db.poll_votes (poll_id, user_id);

create index user_id
    on xclone_db.poll_votes (user_id);

create table if not exists xclone_db.polls
(
    id         int not null auto_increment primary key,
    post_id    int                                 not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    expires_at timestamp                           null
);
create unique index post_id
    on xclone_db.polls (post_id);

create table if not exists xclone_db.post_media
(
    id         int not null auto_increment primary key,
    post_id    int                                 not null,
    file_name  varchar(255)                        null,
    mime_type  varchar(100)                        null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    url        varchar(500)                        null,
    storage_key varchar(512) not null
);

create index post_id
    on xclone_db.post_media (post_id);

create table if not exists xclone_db.posts
(
    id         int not null auto_increment primary key,
    user_id    int                                 not null,
    text       varchar(180)                        null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    parent_id  int                                 null
);

create index fk_posts_parent
    on xclone_db.posts (parent_id);

create index user_id
    on xclone_db.posts (user_id);


create table if not exists xclone_db.retweets
(
    id           int not null auto_increment primary key,
    reference_id int                                 not null,
    retweeter_id int                                 not null,
    type         varchar(45)                         not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null
);

create index fk_retweet_post
    on xclone_db.retweets (reference_id);

create index fk_retweet_user
    on xclone_db.retweets (retweeter_id);

create table if not exists xclone_db.trends
(
    id           bigint unsigned not null auto_increment primary key,
    name         varchar(255)                        not null,
    url          text                                not null,
    tweet_volume int                                 null,
    recorded_at  timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists xclone_db.users
(
    id                  int auto_increment primary key,
    name                varchar(64)                          not null,
    password            varchar(255)                         null,
    bio                 varchar(180)                         null,
    created_at          timestamp  default CURRENT_TIMESTAMP not null,
    email               varchar(255)                         null,
    display_name        varchar(45)                          not null,
    google_id           varchar(100)                         null,
    profile_picture_url varchar(512)                         null,
    banner_image_url    varchar(512)                         null,
    pfp_key varchar(512) not null,
    banner_key varchar(512) not null,
    pinned_post_id      int                                  null,
    verified            tinyint(1) default 0                 not null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint google_id
        unique (google_id),
    constraint id_UNIQUE
        unique (id),
    constraint username_UNIQUE
        unique (name)
);

create table if not exists xclone_db.follows
(
    id          int auto_increment primary key,
    follower_id int not null,
    followed_id int not null,
    constraint follows_ibfk_1
        foreign key (follower_id) references xclone_db.users (id)
            on delete cascade,
    constraint follows_ibfk_2
        foreign key (followed_id) references xclone_db.users (id)
            on delete cascade
);

create index followed_id
    on xclone_db.follows (followed_id);

create index follower_id
    on xclone_db.follows (follower_id);

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE;

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_parent
        FOREIGN KEY (parent_id) REFERENCES posts(id)
            ON DELETE SET NULL;


ALTER TABLE likes
    ADD CONSTRAINT fk_likes_user
        FOREIGN KEY (liker_id) REFERENCES users(id)
            ON DELETE CASCADE;

ALTER TABLE likes
    ADD CONSTRAINT fk_likes_post
        FOREIGN KEY (post_id) REFERENCES posts(id)
            ON DELETE CASCADE;


ALTER TABLE bookmarks
    ADD CONSTRAINT fk_bookmarks_user
        FOREIGN KEY (bookmarked_by) REFERENCES users(id)
            ON DELETE CASCADE;

ALTER TABLE bookmarks
    ADD CONSTRAINT fk_bookmarks_post
        FOREIGN KEY (bookmarked_post) REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE retweets
    ADD CONSTRAINT fk_retweets_post
        FOREIGN KEY (reference_id) REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE retweets
    ADD CONSTRAINT fk_retweets_user
        FOREIGN KEY (retweeter_id) REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE post_media
    ADD CONSTRAINT fk_post_media_post
        FOREIGN KEY (post_id) REFERENCES posts(id)
            ON DELETE CASCADE;


ALTER TABLE feed_entry
    ADD CONSTRAINT fk_feed_entry_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE;

ALTER TABLE feed_entry
    ADD CONSTRAINT fk_feed_entry_post
        FOREIGN KEY (post_id) REFERENCES posts(id)
            ON DELETE CASCADE;


ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_receiver
        FOREIGN KEY (receiver_id) REFERENCES users(id)
            ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_sender
        FOREIGN KEY (sender_id) REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE users
    ADD CONSTRAINT fk_users_pinned_post
        FOREIGN KEY (pinned_post_id) REFERENCES posts(id)
            ON DELETE SET NULL;