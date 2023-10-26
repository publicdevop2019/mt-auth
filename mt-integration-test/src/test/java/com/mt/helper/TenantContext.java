package com.mt.helper;

import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.User;
import java.util.List;
import lombok.Data;

@Data
public class TenantContext {
    private User creator;
    private Project project;
    private Client loginClient;
    private List<User> users;
}
