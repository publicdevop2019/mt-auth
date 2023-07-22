package com.mt.helper.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectAdmin {
    private String id;
    private String email;

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
