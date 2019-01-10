package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;

import java.io.Serializable;

public class CreateAccountCommit extends AccountCommit implements Serializable {
    private String userId;

    public CreateAccountCommit(String userId){
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public void apply(AccountIMD imd){
        String time = Long.toString(System.currentTimeMillis());
        String accountId= time + this.getUserId();
        Account newAccount= new Account(accountId,this.getUserId(),this.getCUID());
        imd.addAccount(newAccount);
        imd.addCommitStatus(new AccountCommitStatus(this.getCUID(), "Done"));
        imd.addAccountCommitResponse(new CreateAccountCommitResponse(this, accountId));
    }
}
