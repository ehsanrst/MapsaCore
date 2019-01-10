package com.mapsa.core.commits.card;

import java.io.Serializable;

public class AddCardCommit extends CardCommit implements Serializable {
    private String accountId;
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
