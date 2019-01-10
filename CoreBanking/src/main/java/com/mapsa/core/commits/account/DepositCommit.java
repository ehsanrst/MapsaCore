package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;

import java.io.Serializable;

public class DepositCommit extends AccountCommit implements Serializable {
    private String accountId;
    private String amount;

    public DepositCommit(String accountId, String amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public void apply(AccountIMD accountIMD) {
        Account account = accountIMD.findAccountById(this.getAccountId());
        if (account == null || account.getLastCommitId().equals(this.getCUID())) {
            accountIMD.addCommitStatus(new AccountCommitStatus(this.getCUID(), "failed"));
            accountIMD.addAccountCommitResponse(new DepositCommitResponse(this, false));
        } else if (account.getActive() && !account.getBlocked()) {
            if (account.deposit(this.getAmount(), this.getCUID())) {
                accountIMD.addAccount(account);
                accountIMD.addCommitStatus(new AccountCommitStatus(this.getCUID(), "done"));
                accountIMD.addAccountCommitResponse(new DepositCommitResponse(this, true));
            } else {
                accountIMD.addCommitStatus(new AccountCommitStatus(this.getCUID(), "failed"));
                accountIMD.addAccountCommitResponse(new DepositCommitResponse(this, false));
            }
        } else {
            accountIMD.addCommitStatus(new AccountCommitStatus(this.getCUID(), "failed"));
            accountIMD.addAccountCommitResponse(new DepositCommitResponse(this, false));
        }
    }
}

