package com.mapsa.core.commits.account;

import java.io.Serializable;

public class AccountTransactionCommit extends AccountCommit implements Serializable {
    private String accountId;
    private int numberOfTransactions;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }
}
