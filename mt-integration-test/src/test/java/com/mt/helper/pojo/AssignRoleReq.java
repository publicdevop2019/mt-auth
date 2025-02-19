package com.mt.helper.pojo;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class AssignRoleReq {
    private Set<String> roleIds = new HashSet<>();
}
