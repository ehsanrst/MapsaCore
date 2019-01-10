package com.mapsa.core.log;

import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.commits.account.*;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class AccountCommitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long logId;
    private String commitId;
    @Lob
    @Column(length = 10000)
    private AccountCommit commit;
    private String time;
    private String previousCommitId;

    public AccountCommitLog() {

    }

    public AccountCommitLog(AccountCommit commit, String previous) {
        this.commit = commit;
        this.commitId = commit.getCUID();
        this.setTime();
        this.previousCommitId = previous;
    }

    public AccountCommit getCommit() {
        return this.commit;
    }

    public String getCommitType() {
        if (commit instanceof DepositCommit) {
            return "Deposit";
        } else if (commit instanceof WithdrawCommit) {
            return "Withdraw";
        } else if (commit instanceof BlockAccountCommit) {
            return "BlockAccount";
        } else if (commit instanceof UnblockAccountCommit) {
            return "UnblockAccount";
        } else if (commit instanceof DeactivateAccountCommit) {
            return "DeactivateAccount";
        } else if (commit instanceof ReactivateAccountCommit) {
            return "ReactivateAccount";
        } else if (commit instanceof CreateAccountCommit) {
            return "CreateAccount";
        } else if (commit instanceof AccountHistoryCommit) {
            return "AccountHistory";
        }
        return null;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        this.time = currentTime;
    }

    public String getPreviousCommitId() {
        return previousCommitId;
    }

}
