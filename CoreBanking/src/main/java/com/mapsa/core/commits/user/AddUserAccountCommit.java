package com.mapsa.core.commits.user;

import java.io.Serializable;

public class AddUserAccountCommit extends UserCommit implements Serializable {
    private String userId;
    private String accountId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
