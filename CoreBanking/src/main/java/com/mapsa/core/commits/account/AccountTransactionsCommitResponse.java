package com.mapsa.core.commits.account;

import java.io.Serializable;
import java.util.List;

public class AccountTransactionsCommitResponse extends AccountCommitResponse implements Serializable {
    private String currentBalance;
    private List<String> transactions;
    private List<String> date;

    public AccountTransactionsCommitResponse(AccountTransactionCommit accountTransactionCommit, String currentBalance, List<String> transactions, List<String> date) {
        this.currentBalance = currentBalance;
        this.transactions = transactions;
        this.date = date;
        super.setCommitId(accountTransactionCommit.getCUID());
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public List<String> getDate() {
        return date;
    }
}
