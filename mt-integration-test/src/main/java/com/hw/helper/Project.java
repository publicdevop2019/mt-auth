package com.hw.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Project {
    private  String name;
    private  String id;
    private  Integer totalUserOwned;
    private  String createdBy;
    private  Long createdAt;
    private String creatorName;
}
