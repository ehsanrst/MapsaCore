package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;

import java.io.Serializable;

public class DeactivateAccountCommit extends AccountCommit implements Serializable {
    private String accountId;

    public DeactivateAccountCommit(String accountId){
        this.accountId= accountId;
    }
    public String getAccountId() {
        return accountId;
    }

    @Override
    public void apply(AccountIMD accountIMD) {
        DeactivateAccountCommit sameCommit=(DeactivateAccountCommit) accountIMD.findCommitById(getCUID());
        DeactivateAccountCommitResponse sameCommitResponse=(DeactivateAccountCommitResponse) accountIMD.findCommitResponseById(getCUID());
        if (sameCommit==null || !sameCommitResponse.isDone()) {
            Account account = accountIMD.findAccountById(getAccountId());
            account.deactive(getCUID());
            accountIMD.addAccount(account);
            AccountCommitStatus commitStatus = new AccountCommitStatus(getCUID(), "done");
            accountIMD.addCommitStatus(commitStatus);
            DeactivateAccountCommitResponse deactivateAccountCommitResponse = new DeactivateAccountCommitResponse(this, true);
            AccountCommitResponse accountCommitResponse = deactivateAccountCommitResponse;
            accountIMD.addAccountCommitResponse(accountCommitResponse);
        }
    }
}
