package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;

import java.io.Serializable;

public class BlockAccountCommit extends AccountCommit implements Serializable {
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public void apply(AccountIMD IMD){
        BlockAccountCommit same_commit =(BlockAccountCommit) IMD.findCommitById(this.getCUID());
        BlockAccountCommitResponse same_commit_response =(BlockAccountCommitResponse) IMD.findCommitResponseById(this.getCUID());
        if (same_commit==null||!same_commit_response.isDone()){
            Account account = IMD.findAccountById(getAccountId());
            account.block(this.getCUID());
            IMD.addAccount(account);
            AccountCommitStatus commitStatus = new AccountCommitStatus(this.getCUID(), "Done");
            IMD.addCommitStatus(commitStatus);
            BlockAccountCommitResponse blockAccountCommitResponse = new BlockAccountCommitResponse(this, true);
            IMD.addAccountCommitResponse(blockAccountCommitResponse);
        }
    }
}
