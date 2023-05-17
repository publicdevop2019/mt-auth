package com.mt.test_case.helper.pojo;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SystemNotification {
    private Long date;
    private String id;
    private String title;
    private Set<String> descriptions;
    private String type;
    private String status;

}
