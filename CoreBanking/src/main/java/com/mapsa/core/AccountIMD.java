package com.mapsa.core;
import com.mapsa.core.account.Account;
import com.mapsa.core.card.Card;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CommitStatus;

public interface AccountIMD {
    abstract void addAccount(Account account);
    abstract Account findAccountById(String id);
    abstract AccountCommit findCommitById(String id);
    abstract void addAccountCommitResponse(AccountCommitResponse accountCommitResponse);
    abstract AccountCommitResponse findCommitResponseById(String id);
    abstract void addCommitStatus(CommitStatus commitStatus);
    abstract CommitStatus findCommitStatusById(String id);
}