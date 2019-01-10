package com.mapsa.core.commits.account;

import java.io.Serializable;

public class ReactivateAccountCommit extends AccountCommit implements Serializable {
    private String  accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
