package com.mapsa.core.log;

import com.mapsa.core.commits.account.*;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class AccountCommitResponseLog {
    @Id
    private String commitId;
    @Lob@Column(length = 10000)
    private AccountCommitResponse response;
    private String time;
    public AccountCommitResponseLog() {
    }

    public AccountCommitResponseLog(AccountCommitResponse response) {
        this.response = response;
        setTime();
        this.commitId = response.getCommitId();
    }

    public String getCommitId() {
        return commitId;
    }

    public AccountCommitResponse getResponse() {
        return response;
    }

    public String getResponseType() {
        if (response instanceof CreateAccountCommitResponse) {
            return "CreateAccountCommit";
        } else if (response instanceof DeactivateAccountCommitResponse) {
            return "DeactivateAccountCommit";
        } else if (response instanceof ReactivateAccountCommitResponse) {
            return "ReactivateAccountCommitResponse";
        } else if (response instanceof BlockAccountCommitResponse) {
            return "BlockAccountCommitResponse";
        } else if (response instanceof AccountHistoryCommitResponse) {
            return "AccountHistoryCommitResponse";
        } else if (response instanceof UnblockAccountCommitResponse) {
            return "UnblockAccountCommitResponse";
        } else if (response instanceof WithdrawCommitResponse) {
            return "WithdrawCommitResponse";
        } else if (response instanceof DepositCommitResponse) {
            return "DepositCommitResponse";
        } else if (response instanceof AccountTransactionsCommitResponse) {
            return "AccountTransactionsCommitResponse";
        }
        return null;
    }

    public void setTime() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        this.time = currentTime;
    }
}
