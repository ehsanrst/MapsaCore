package com.mapsa.core.commits.account;


import java.io.Serializable;

public class AccountHistoryCommit extends AccountCommit implements Serializable {
    private String accountId;
    private int numberOfHistoryEvent;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getNumberOfHistoryEvent() {
        return numberOfHistoryEvent;
    }

    public void setNumberOfHistoryEvent(int numberOfHistoryEvent) {
        this.numberOfHistoryEvent = numberOfHistoryEvent;
    }
}
