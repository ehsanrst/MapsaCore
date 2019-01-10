package com.mapsa.core.commits.account;

import java.io.Serializable;

public class UnblockAccountCommitResponse extends AccountCommitResponse implements Serializable {
    private boolean done;

    public UnblockAccountCommitResponse(UnblockAccountCommit unblockAccountCommit, boolean done) {
        this.done = done;
        super.setCommitId(unblockAccountCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}
