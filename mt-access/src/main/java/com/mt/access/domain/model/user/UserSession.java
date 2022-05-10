package com.mt.access.domain.model.user;

public class UserSession {
    private final String ipAddress;

    public UserSession(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {

        return ipAddress;
    }
}
