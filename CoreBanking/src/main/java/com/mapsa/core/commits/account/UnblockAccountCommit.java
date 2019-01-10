package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;

import java.io.Serializable;

public class UnblockAccountCommit extends AccountCommit implements Serializable {
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
	
	@Override
    public void apply(AccountIMD IMD) {
        AccountCommit same_commit = IMD.findCommitById(getCUID());
        UnblockAccountCommitResponse same_commitResponse =(UnblockAccountCommitResponse) IMD.findCommitResponseById(getCUID());
        if (same_commit==null||!same_commitResponse.isDone()) {
            Account account = IMD.findAccountById(accountId);
            account.unblock(getCUID());
            AccountCommitStatus commitStatus = new AccountCommitStatus(getCUID(), "done");
            AccountCommitResponse commitResponse = new UnblockAccountCommitResponse(this, true);
            IMD.addAccount(account);
            IMD.addCommitStatus(commitStatus);
            IMD.addAccountCommitResponse(commitResponse);
        }
    }
}
