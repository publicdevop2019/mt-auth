package com.hw.helper;

import lombok.Data;

@Data
public class CreateCommentCommand {
    private String content;
    private String replyTo;
}
