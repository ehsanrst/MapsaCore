package com.mapsa.core.commits.account;

import java.io.Serializable;
import java.util.List;

public class AccountHistoryCommitResponse extends AccountCommitResponse implements Serializable {
    private List<String> event;
    private List<String> date;

    public AccountHistoryCommitResponse(AccountHistoryCommit accountHistoryCommit, List<String> event, List<String> date) {
        this.event = event;
        this.date = date;
        super.setCommitId(accountHistoryCommit.getCUID());
    }

    public List<String> getEvent() {
        return event;
    }

    public List<String> getDate() {
        return date;
    }
}
