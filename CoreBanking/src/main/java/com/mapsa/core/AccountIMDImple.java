package com.mapsa.core;

import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.card.Card;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.account.BlockAccountCommit;
import com.mapsa.core.commits.account.BlockAccountCommitResponse;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import java.sql.SQLException;
import java.sql.SQLException;

public class AccountIMDImple implements AccountIMD {
    AccountCommitLogger logger = new AccountCommitLogger();
    AccountDAL accountDAL=new AccountDAL();
    @Override
    public void addAccount(Account account) {
        accountDAL.saveAccount(account);
    }

    @Override
    public Account findAccountById(String id) {
        return accountDAL.loadAccount(id);
    }

    @Override
    public AccountCommit findCommitById(String id) {
        AccountCommitLog accountCommitLog= logger.findCommitById(id);
        if (accountCommitLog==null){
            return null;
        }
        return accountCommitLog.getCommit();
    }

    @Override
    public void addAccountCommitResponse(AccountCommitResponse accountCommitResponse) {
        AccountCommitResponseLog accountCommitResponseLog=new AccountCommitResponseLog(accountCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);
    }

    @Override
    public AccountCommitResponse findCommitResponseById(String id) {
        AccountCommitResponseLog accountCommitResponseLog=logger.findCommitResponseByCommitId(id);
        if (accountCommitResponseLog==null){
            return null;
        }
        return accountCommitResponseLog.getResponse();
    }

    @Override
    public void addCommitStatus(CommitStatus commitStatus) {
        logger.saveCommitStatus(commitStatus);
    }

    @Override
    public AccountCommitStatus findCommitStatusById(String id) {
        AccountCommitStatus commitStatus = logger.findCommitStatus(id);
        return commitStatus;
    }
}

