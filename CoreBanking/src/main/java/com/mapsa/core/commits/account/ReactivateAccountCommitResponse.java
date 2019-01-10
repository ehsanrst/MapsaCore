package com.mapsa.core.commits.account;

import java.io.Serializable;

public class ReactivateAccountCommitResponse extends AccountCommitResponse implements Serializable {
    private boolean done;

    public ReactivateAccountCommitResponse(ReactivateAccountCommit reactivateAccountCommit, boolean done) {
        this.done = done;
        super.setCommitId(reactivateAccountCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}
