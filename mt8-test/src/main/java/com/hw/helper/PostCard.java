package com.hw.helper;

import lombok.Data;

import java.util.Date;

@Data
public class PostCard {
    private Long id;
    private String title;
    private String topic;
    private Date publishedAt;
    private String publisherId;
    private Long views;
    private Long comments;

}
