package com.mt.helper.pojo;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BellNotification {
    private Long date;
    private String title;
    private String id;
    private Set<String> descriptions;
}
