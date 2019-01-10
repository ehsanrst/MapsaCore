package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;

import java.io.Serializable;

public class WithdrawCommit extends AccountCommit implements Serializable {
    private String accountId;
    private String amount;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public void apply(AccountIMD IMD) {
        Account account = IMD.findAccountById(accountId);
        if(account == null){
            //fails because account dose't exist
            AccountCommitStatus accountCommitStatus = new AccountCommitStatus(account.getAccountId(), "failed");
            IMD.addCommitStatus(accountCommitStatus);
            AccountCommitResponse commitResponse = new WithdrawCommitResponse(this,false);
            IMD.addAccountCommitResponse(commitResponse);
        }else {
            if (account.getActive() && !account.getBlocked()) {
                account.withdraw(getAmount(), getCUID());
                IMD.addAccount(account);
                String previousCommitID=account.getLastCommitId();
                AccountCommitLog accountCommitLog = new AccountCommitLog(this, previousCommitID);
                //save accountCommitLog
                AccountCommitStatus accountCommitStatus = new AccountCommitStatus(account.getAccountId(), "done");
                IMD.addCommitStatus(accountCommitStatus);
                AccountCommitResponse commitResponse = new WithdrawCommitResponse(this,true);
                IMD.addAccountCommitResponse(commitResponse);
            }else{
                //add to response that commit failed due to block or active status
                AccountCommitStatus accountCommitStatus = new AccountCommitStatus(account.getAccountId(), "failed");
                IMD.addCommitStatus(accountCommitStatus);
                AccountCommitResponse commitResponse = new WithdrawCommitResponse(this,false);
                IMD.addAccountCommitResponse(commitResponse);
            }
        }
    }
}
