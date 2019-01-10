package com.mapsa.core.commits.user;

import java.io.Serializable;

public class ReactivateUserCommit extends UserCommit implements Serializable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
