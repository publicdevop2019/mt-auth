package com.hw.helper;

import java.util.List;
import lombok.Data;

@Data
public class Notification {
    private long date;
    private List<String> descriptions;
    private String id;
    private String title;
}
