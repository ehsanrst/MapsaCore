package com.mapsa.core.commits.user;

import java.io.Serializable;

public class GetUserAccountsCommit extends UserCommit implements Serializable {
    private String userId;

    public GetUserAccountsCommit(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
