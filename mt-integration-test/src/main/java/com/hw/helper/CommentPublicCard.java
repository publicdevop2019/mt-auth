package com.hw.helper;

import lombok.Data;

import java.util.Date;

@Data
public class CommentPublicCard {
    private Long id;
    private String content;
    private String replyTo;
    private Date publishedAt;
    private String publishedBy;
    private Long likeNum;
    private Long dislikeNum;

}