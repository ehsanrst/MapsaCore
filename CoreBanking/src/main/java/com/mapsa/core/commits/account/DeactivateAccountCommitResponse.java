package com.mapsa.core.commits.account;

import java.io.Serializable;

public class DeactivateAccountCommitResponse extends AccountCommitResponse implements Serializable {
    private boolean done;

    public DeactivateAccountCommitResponse(DeactivateAccountCommit deactivateAccountCommit, boolean done) {
        this.done = done;
        super.setCommitId(deactivateAccountCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}
