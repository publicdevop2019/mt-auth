package com.mt.test_case.helper;

import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.User;
import java.util.List;
import lombok.Data;

@Data
public class TenantContext {
    private User creator;
    private Project project;
    private String loginClientId;
    private List<User> users;
}
