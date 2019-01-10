package com.mapsa.core.commits.account;

import java.io.Serializable;

public class WithdrawCommitResponse extends AccountCommitResponse implements Serializable {
    private Boolean done;

    public WithdrawCommitResponse(WithdrawCommit withdrawCommit, Boolean done) {
        this.done = done;
        super.setCommitId(withdrawCommit.getCUID());

    }

    public Boolean getDone() {
        return done;
    }

}

