package com.hw.helper;

import lombok.Data;

import java.util.Date;

@Data
public class PostDetailRepresentation {
    private Long id;
    private String title;
    private String topic;
    private Date publishedAt;
    private String publishedBy;
    private String content;
    private Long likeNum;
    private Boolean userModified;
    private Long dislikeNum;

}
